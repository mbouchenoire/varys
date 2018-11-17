package org.varys.gitlab.notifier;

import org.varys.common.service.CacheService;
import org.varys.common.service.Log;
import org.varys.common.service.NotificationService;
import org.varys.common.service.NotifierModule;
import org.varys.git.GitService;
import org.varys.gitlab.api.GitLabApi;
import org.varys.gitlab.model.GitLabMergeRequest;
import org.varys.gitlab.model.GitLabMergeRequestNotifierConfig;
import org.varys.gitlab.model.GitLabMergeRequestState;
import org.varys.gitlab.model.notification.MergeRequestUpdateNotificationChain;
import org.varys.gitlab.model.notification.NewMergeRequestNotification;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GitLabMergeRequestNotifier implements NotifierModule {

    private GitLabMergeRequestNotifierConfig config;
    private final GitLabApi gitLabApi;
    private final GitService gitService;
    private final CacheService cacheService;
    private final NotificationService notificationService;

    public GitLabMergeRequestNotifier(
            GitLabMergeRequestNotifierConfig config,
            GitLabApi gitLabApi,
            GitService gitService,
            CacheService cacheService,
            NotificationService notificationService) {

        this.config = config;
        this.gitLabApi = gitLabApi;
        this.gitService = gitService;
        this.cacheService = cacheService;
        this.notificationService = notificationService;
    }

    @Override
    public void startModule() {
        boolean running = true;

        while (running) {
            final LocalDateTime start = LocalDateTime.now();

            Log.info("Starting notification process for the GitLab module...");
            this.notifyUser();

            final LocalDateTime end = LocalDateTime.now();
            final Duration between = Duration.between(start, end);
            Log.debug("GitLab module notification process duration: {} seconds", between.getSeconds());

            try {
                Thread.sleep(this.config.getNotificationsConfig().getPeriodSeconds() * 1000);
            } catch (InterruptedException e) {
                Log.error(e, "Failed to pause the GitLab module");
                running = false;
            }
        }

        Log.warn("Stopped GitLab module");
    }

    private void notifyUser() {
        final List<GitLabMergeRequest> liveMergeRequests =
                this.gitLabApi.getMergeRequests(GitLabMergeRequestState.OPENED);

        Log.debug("Processing {} (fetched) GitLab merge request(s) (opened)", liveMergeRequests.size());

        liveMergeRequests.parallelStream()
                .filter(this::isRelevant)
                .forEach(this::notifyUserLiveMergeRequest);

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
                    ).map(latestVersion -> {
                        this.notififyUpdate(latestVersion, notOpenedAnymoreCachedMergeRequest);
                        return latestVersion;
                    });
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(GitLabMergeRequest::isDone)
                .forEach(this::deleteCache);
    }

    private void deleteCache(GitLabMergeRequest mergeRequest) {
        this.cacheService.delete(cachePath(mergeRequest));
    }

    private void notifyUserLiveMergeRequest(GitLabMergeRequest liveMergeRequest) {
        final GitLabMergeRequest mergeRequest = this.getCache(liveMergeRequest)
                .map(cachedMergeRequest -> {
                    this.notififyUpdate(liveMergeRequest, cachedMergeRequest);
                    return liveMergeRequest;
                })
                .orElseGet(() -> {
                    Log.debug("Could not find a cached version of merge request: {}", liveMergeRequest);
                    this.notificationService.send(new NewMergeRequestNotification(liveMergeRequest));
                    return liveMergeRequest;
                });

        this.cache(mergeRequest);
    }

    private boolean isRelevant(GitLabMergeRequest mergeRequest) {
        final String sourceBranch = mergeRequest.getSourceBranch();
        final String targetBranch = mergeRequest.getTargetBranch();

        final boolean relevant = this.gitService.hasLocalBranch(sourceBranch)
                || this.gitService.hasLocalBranch(targetBranch);

        Log.debug("{} is not a relevant merge request", mergeRequest.getIdentifier());

        return relevant;
    }

    private List<GitLabMergeRequest> getCachedMergeRequests() {
        //noinspection ConstantConditions

        return Arrays.stream(this.cacheService.getRootDirectory().listFiles())
                .flatMap(domainDirectory -> Arrays.stream(domainDirectory.listFiles()))
                .flatMap(projectDirectory -> Arrays.stream(projectDirectory.listFiles()))
                .map(mergeRequestFile -> this.cacheService.get(mergeRequestFile, GitLabMergeRequest.class))
                .collect(Collectors.toList());
    }

    private void notififyUpdate(
            GitLabMergeRequest latestVersion,
            GitLabMergeRequest previousVersion) {

        Log.debug("Notifying differences for merge request: {}...", latestVersion.getIdentifier());

        final MergeRequestUpdateNotificationChain updateNotification =
                new MergeRequestUpdateNotificationChain(latestVersion, previousVersion);

        if (updateNotification.shouldNotify()) {
            this.notificationService.send(updateNotification);
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
        return "GitLabMergeRequestNotifier{" +
                "gitLabApiV4=" + gitLabApi +
                '}';
    }
}
