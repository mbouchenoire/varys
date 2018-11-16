package org.varys.gitlab.notifier;

import org.varys.common.service.Log;
import org.varys.common.service.NotifierModule;
import org.varys.common.service.CacheService;
import org.varys.common.service.NotificationService;
import org.varys.git.GitService;
import org.varys.gitlab.api.GitLabApi;
import org.varys.gitlab.model.GitLabMergeRequestDetails;
import org.varys.gitlab.model.GitLabMergeRequestNotifierConfig;
import org.varys.gitlab.model.GitLabMergeRequestState;

import java.awt.*;
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
        final List<GitLabMergeRequestDetails> liveMergeRequests =
                this.gitLabApi.getMergeRequests(GitLabMergeRequestState.OPENED);

        Log.debug("Processing {} (fetched) GitLab merge request(s) (opened)", liveMergeRequests.size());

        liveMergeRequests.parallelStream()
                .filter(this::isRelevant)
                .forEach(this::notifyUserLiveMergeRequest);

        final List<GitLabMergeRequestDetails> cachedMergeRequests = this.getCachedMergeRequests();

        Log.debug("Processing {} (cached) GitLab merge request(s)", cachedMergeRequests.size());

        final Predicate<GitLabMergeRequestDetails> notOpenedAnymore = mergeRequest -> liveMergeRequests.stream()
                .noneMatch(openedMergeRequest -> openedMergeRequest.isSameMergeRequest(mergeRequest));

        cachedMergeRequests.parallelStream()
                .filter(notOpenedAnymore)
                .map(notOpenedAnymoreCachedMergeRequest -> {
                    Log.debug("Found a cached merge request that is not opened anymore: {}",
                            notOpenedAnymoreCachedMergeRequest.getIdentifier());

                    return this.gitLabApi.getMergeRequest(
                            notOpenedAnymoreCachedMergeRequest.getProject().getId(),
                            notOpenedAnymoreCachedMergeRequest.getId(),
                            notOpenedAnymoreCachedMergeRequest.getIid())
                            .map(latestVersion -> notifyDiff(latestVersion, notOpenedAnymoreCachedMergeRequest));
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(GitLabMergeRequestDetails::isDone)
                .forEach(this::deleteCache);
    }

    private void deleteCache(GitLabMergeRequestDetails mergeRequest) {
        this.cacheService.delete(cachePath(mergeRequest));
    }

    private void notifyUserLiveMergeRequest(GitLabMergeRequestDetails liveMergeRequest) {
        final GitLabMergeRequestDetails mergeRequest = this.getCache(liveMergeRequest)
                .map(cachedMergeRequest -> this.notifyDiff(liveMergeRequest, cachedMergeRequest))
                .orElseGet(() -> {
                    Log.debug("Could not find a cached version of merge request: {}", liveMergeRequest);
                    return this.notify("New merge request", liveMergeRequest);
                });

        this.cache(mergeRequest);
    }

    private boolean isRelevant(GitLabMergeRequestDetails mergeRequest) {
        final String sourceBranch = mergeRequest.getSourceBranch();
        final String targetBranch = mergeRequest.getTargetBranch();

        final boolean relevant = this.gitService.hasLocalBranch(sourceBranch)
                || this.gitService.hasLocalBranch(targetBranch);

        Log.debug("{} is not a relevant merge request", mergeRequest.getIdentifier());

        return relevant;
    }

    private List<GitLabMergeRequestDetails> getCachedMergeRequests() {
        //noinspection ConstantConditions

        return Arrays.stream(this.cacheService.getRootDirectory().listFiles())
                .flatMap(domainDirectory -> Arrays.stream(domainDirectory.listFiles()))
                .flatMap(projectDirectory -> Arrays.stream(projectDirectory.listFiles()))
                .map(mergeRequestFile -> this.cacheService.get(mergeRequestFile, GitLabMergeRequestDetails.class))
                .collect(Collectors.toList());
    }

    private static String formatMergeRequestDescription(GitLabMergeRequestDetails mergeRequest) {
        final String descriptionTemplate =
                "%s\n" +
                "%s\n" +
                "%s into %s\n" +
                "by %s for %s";

        return String.format(descriptionTemplate,
                mergeRequest.getTitle(),
                mergeRequest.getIdentifier(),
                mergeRequest.getSourceBranch(), mergeRequest.getTargetBranch(),
                mergeRequest.getAuthor().getName(), mergeRequest.getAssignee().getName()
        );
    }

    private void notify(
            String title,
            TrayIcon.MessageType messageType,
            GitLabMergeRequestDetails mergeRequest) {

        final String description = formatMergeRequestDescription(mergeRequest);
        this.notificationService.notify(title, description, messageType);
    }

    private GitLabMergeRequestDetails notify(
            String title,
            GitLabMergeRequestDetails mergeRequest) {

        this.notify(title, TrayIcon.MessageType.INFO, mergeRequest);

        return mergeRequest;
    }

    private void notifyWarning(
            String title,
            GitLabMergeRequestDetails mergeRequest) {

        this.notify(title, TrayIcon.MessageType.WARNING, mergeRequest);
    }

    private GitLabMergeRequestDetails notifyDiff(
            GitLabMergeRequestDetails latestVersion, GitLabMergeRequestDetails oldVersion) {

        Log.debug("Notifying differences for merge request: {}...", latestVersion.getIdentifier());

        if (latestVersion.isDone() && !oldVersion.isDone()) {
            if (latestVersion.isMerged()) {
                this.notify("Merge request has been merged", latestVersion);
            } else if (latestVersion.isClosed()) {
                this.notifyWarning("Merge request has been closed", latestVersion);
            } else {
                this.notifyWarning("Merge request changed status", latestVersion);
            }
        } else {
            if (!latestVersion.sameAssignee(oldVersion)) {
                this.notifyWarning("Merge request changed assignee", latestVersion);
            }

            final long addedCommitsCount = latestVersion.addedCommitsCount(oldVersion);

            if (addedCommitsCount > 0) {
                final String s = addedCommitsCount > 1 ? "s" : "";
                final String title = String.format("%d new commit%s on merge request", addedCommitsCount, s);
                this.notify(title, TrayIcon.MessageType.INFO, latestVersion);
            }

            final long addedUserNotesCount = latestVersion.addedUserNotesCount(oldVersion);

            if (addedUserNotesCount > 0) {
                final String s = addedUserNotesCount > 1 ? "s" : "";
                final String title = String.format("%d new comment%s on merge request", addedUserNotesCount, s);
                this.notify(title, TrayIcon.MessageType.INFO, latestVersion);
            }
        }

        return latestVersion;
    }

    private void cache(GitLabMergeRequestDetails mergeRequest) {
        final String mergeRequestCachePath = this.cachePath(mergeRequest);
        this.cacheService.save(mergeRequestCachePath, mergeRequest);
    }

    private Optional<GitLabMergeRequestDetails> getCache(GitLabMergeRequestDetails mergeRequest) {
        final String mergeRequestCachePath = this.cachePath(mergeRequest);
        return this.cacheService.get(mergeRequestCachePath, GitLabMergeRequestDetails.class);
    }

    private String cachePath(long projectId, long mergeRequestIid) {
        return this.gitLabApi.getDomainName() + File.separator
                + projectId + File.separator
                + mergeRequestIid;
    }

    private String cachePath(GitLabMergeRequestDetails mergeRequest) {
        return this.cachePath(mergeRequest.getProject().getId(), mergeRequest.getIid());
    }

    @Override
    public String toString() {
        return "GitLabMergeRequestNotifier{" +
                "gitLabApiV4=" + gitLabApi +
                '}';
    }
}
