package org.varys.gitlab;

import org.varys.common.service.CacheService;
import org.varys.common.service.Log;
import org.varys.common.service.NotificationService;
import org.varys.common.service.NotifierModule;
import org.varys.common.service.RestApiService;
import org.varys.gitlab.api.GitLabApi;
import org.varys.gitlab.model.GitLabMergeRequest;
import org.varys.gitlab.model.GitLabMergeRequestState;
import org.varys.gitlab.model.GitLabNotifierConfig;
import org.varys.gitlab.model.GitLabUser;
import org.varys.gitlab.model.notification.MergeRequestUpdateNotificationChain;
import org.varys.gitlab.model.notification.NewMergeRequestNotification;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class GitLabNotifier implements NotifierModule {

    private GitLabNotifierConfig config;
    private final GitLabApi gitLabApi;
    private final RestApiService restApiService;
    private final CacheService cacheService;
    private final NotificationService notificationService;

    public GitLabNotifier(
            GitLabNotifierConfig config,
            GitLabApi gitLabApi,
            CacheService cacheService,
            NotificationService notificationService) {

        this.config = config;
        this.gitLabApi = gitLabApi;
        this.restApiService = new RestApiService(cacheService, notificationService);
        this.cacheService = cacheService;
        this.notificationService = notificationService;
    }

    @Override
    public String getName() {
        return "GitLab";
    }

    @Override
    public long getPeriodSeconds() {
        return this.config.getNotificationsConfig().getPeriodSeconds();
    }

    @Override
    public void iterate() {
        if (this.restApiService.isOffline()) {
            Log.warn("No internet access, aborting GitLab notification iteration");
            return;
        }

        final boolean apiIsOnline = this.restApiService.notifyApiStatus(this.gitLabApi);

        if (!apiIsOnline) {
            Log.error("GitLab API is down");
            return;
        }

        final List<GitLabMergeRequest> liveMergeRequests =
                this.gitLabApi.getMergeRequests(GitLabMergeRequestState.OPENED);

        Log.debug("Processing {} (fetched) GitLab merge request(s) (opened)", liveMergeRequests.size());

        final GitLabUser myself = this.gitLabApi.getUser();

        liveMergeRequests.parallelStream()
                .filter(mr -> this.userFilter(mr, myself))
                .map(mr -> this.notifyUser(mr, myself))
                .forEach(this::cache);

        final List<GitLabMergeRequest> cachedMergeRequests = this.getCachedMergeRequests();

        Log.debug("Processing {} (cached) GitLab merge request(s)", cachedMergeRequests.size());

        final Predicate<GitLabMergeRequest> notOpenedAnymore = mergeRequest -> liveMergeRequests.stream()
                .noneMatch(openedMergeRequest -> openedMergeRequest.isSameMergeRequest(mergeRequest));

        cachedMergeRequests.parallelStream()
                .filter(notOpenedAnymore)
                .map(notOpenedAnymoreCachedMergeRequest -> {
                    Log.debug("Found a cached merge request that is not opened anymore: {}",
                            notOpenedAnymoreCachedMergeRequest.getIdentifier());

                    return this.gitLabApi.getMergeRequest(
                            notOpenedAnymoreCachedMergeRequest.getProject().getId(),
                            notOpenedAnymoreCachedMergeRequest.getId(),
                            notOpenedAnymoreCachedMergeRequest.getIid()
                    ).map(latestVersion ->
                            this.notififyPotentialUpdate(latestVersion, notOpenedAnymoreCachedMergeRequest, myself));

                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(GitLabMergeRequest::isDone)
                .forEach(this::deleteCache);
    }

    private void deleteCache(GitLabMergeRequest mergeRequest) {
        this.cacheService.delete(cachePath(mergeRequest));
    }

    private GitLabMergeRequest notifyUser(GitLabMergeRequest liveMergeRequest, GitLabUser myself) {
        return this.getCache(liveMergeRequest)
                .map(cachedMergeRequest -> this.notififyPotentialUpdate(liveMergeRequest, cachedMergeRequest, myself))
                .orElseGet(() -> {
                    Log.debug("Could not find a cached version of merge request: {}", liveMergeRequest);

                    if (liveMergeRequest.isWip()) {
                        return liveMergeRequest; // We don't notify new WIP merge requests
                    }

                    if (liveMergeRequest.getAssignee().equals(myself)) {
                        this.notificationService.send(new NewMergeRequestNotification(liveMergeRequest));
                        return liveMergeRequest.notified();
                    }

                    return liveMergeRequest;
                });
    }

    private boolean userFilter(GitLabMergeRequest mergeRequest, GitLabUser myself) {
        final boolean assignedToMeOnly = config.getNotificationsConfig().getFilters().isInvolvingMyselfOnly();

        if (!assignedToMeOnly) {
            return true;
        }

        return mergeRequest.isRelevantUser(myself);
    }

    private List<GitLabMergeRequest> getCachedMergeRequests() {
        //noinspection ConstantConditions
        final File[] rootDirectoryFiles = this.cacheService.getRootDirectory().listFiles();

        return Arrays.stream(rootDirectoryFiles != null ? rootDirectoryFiles : new File[0])
                .flatMap(this::mapDirectoryFileStream)
                .flatMap(this::mapDirectoryFileStream)
                .map(mergeRequestFile -> this.cacheService.get(mergeRequestFile, GitLabMergeRequest.class))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Stream<? extends File> mapDirectoryFileStream(File domainDirectory) {
        final File[] array = domainDirectory.listFiles();
        return Arrays.stream(array != null ? array : new File[0]);
    }

    private GitLabMergeRequest notififyPotentialUpdate(
            GitLabMergeRequest latestVersion,
            GitLabMergeRequest previousVersion,
            GitLabUser myself) {

        Log.debug("Notifying differences for merge request: {}...", latestVersion.getIdentifier());

        final MergeRequestUpdateNotificationChain updateNotification = new MergeRequestUpdateNotificationChain(
                latestVersion,
                previousVersion,
                myself,
                this.config.getNotificationsConfig().getFilters().getHoursBeforeReminder());

        if (updateNotification.shouldNotify()) {
            this.notificationService.send(updateNotification);
            return latestVersion.notified();
        } else {
            return latestVersion;
        }
    }

    private void cache(GitLabMergeRequest mergeRequest) {
        final String mergeRequestCachePath = this.cachePath(mergeRequest);
        this.cacheService.save(mergeRequestCachePath, mergeRequest);
    }

    private Optional<GitLabMergeRequest> getCache(GitLabMergeRequest mergeRequest) {
        final String mergeRequestCachePath = this.cachePath(mergeRequest);
        return this.cacheService.get(mergeRequestCachePath, GitLabMergeRequest.class);
    }

    private String cachePath(long projectId, long mergeRequestIid) {
        return this.gitLabApi.getDomainName() + File.separator
                + projectId + File.separator
                + mergeRequestIid;
    }

    private String cachePath(GitLabMergeRequest mergeRequest) {
        return this.cachePath(mergeRequest.getProject().getId(), mergeRequest.getIid());
    }

    @Override
    public String toString() {
        return "GitLabNotifier{" +
                "gitLabApi=" + gitLabApi +
                '}';
    }
}
