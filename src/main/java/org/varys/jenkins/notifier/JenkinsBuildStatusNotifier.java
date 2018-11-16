package org.varys.jenkins.notifier;


import org.varys.common.service.CacheService;
import org.varys.common.service.Log;
import org.varys.common.service.NotificationService;
import org.varys.common.service.NotifierModule;
import org.varys.git.GitService;
import org.varys.jenkins.api.JenkinsApi;
import org.varys.jenkins.model.JenkinsBuild;
import org.varys.jenkins.model.JenkinsBuildListItem;
import org.varys.jenkins.model.JenkinsBuildNotifierConfig;
import org.varys.jenkins.model.JenkinsBuildNumber;
import org.varys.jenkins.model.JenkinsNode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class JenkinsBuildStatusNotifier implements NotifierModule {

    private static final Comparator<JenkinsBuildNumber> LATEST_BUILD_FIRST_COMPARATOR =
            (build1, build2) -> Long.compare(build2.getNumber(), build1.getNumber());

    private final JenkinsApi jenkinsApi;
    private final JenkinsBuildNotifierConfig config;
    private final GitService gitService;
    private final CacheService cacheService;
    private final NotificationService notificationService;

    public JenkinsBuildStatusNotifier(
            JenkinsBuildNotifierConfig config,
            GitService gitService,
            CacheService cacheService,
            NotificationService notificationService) {

        this.gitService = gitService;
        this.jenkinsApi = new JenkinsApi(config.getJenkinsApiConfig());
        this.config = config;
        this.cacheService = cacheService;
        this.notificationService = notificationService;
    }

    @Override
    public void startModule() {
        boolean running = true;

        while (running) {
            final LocalDateTime start = LocalDateTime.now();

            Log.info("Starting notification process for the Jenkins module...");
            this.notifyUser();

            final LocalDateTime end = LocalDateTime.now();
            final Duration between = Duration.between(start, end);
            Log.debug("Jenkins module notification process duration: {} seconds", between.getSeconds());

            try {
                Thread.sleep(this.config.getNotificationsConfig().getPeriodSeconds() * 1000);
            } catch (InterruptedException e) {
                Log.error(e, "Failed to pause the Jenkins module");
                running = false;
            }
        }

        Log.warn("Stopped Jenkins module");
    }

    private void notifyUser() {
        this.jenkinsApi.getRootNode().ifPresent(this::notifyUser);
    }

    private void notifyUser(JenkinsNode jenkinsNode) {
        final Runnable traverseJenkinsNode = traverseJenkinsNode(jenkinsNode);
        final Runnable notifyLastNewFailedBuild = notifyLastNewFailedBuild(jenkinsNode);

        Arrays.asList(traverseJenkinsNode, notifyLastNewFailedBuild).parallelStream()
                .forEach(Runnable::run);
    }

    private JenkinsBuild notifyUser(JenkinsBuild jenkinsBuild) {
        final String title = jenkinsBuild.getResult().getAdjective() + " Jenkins build";

        final String description =
                jenkinsBuild.getFullDisplayName() + "\n" +
                jenkinsBuild.getCause().orElse("Unknown cause");

        this.notificationService.notify(
                title,
                description,
                jenkinsBuild.getResult().getMessageType());

        return jenkinsBuild;
    }

    private Runnable traverseJenkinsNode(JenkinsNode jenkinsNode) {
        final long childrenCount = jenkinsNode.getChildren().size();

        if (childrenCount > 0) {
            Log.debug("Processing {} children of Jenkins node '{}'",
                    jenkinsNode.getChildren().size(), jenkinsNode.getDisplayName());
        } else {
            Log.trace("Jenkins node '{}' has no child to process", jenkinsNode.getDisplayName());
        }

        return () -> jenkinsNode.getChildren().parallelStream()
                .forEach(child -> this.jenkinsApi.getNode(child.getApiUrl()).ifPresent(this::notifyUser));
    }

    private boolean branchFilter(JenkinsBuild build) {
        final boolean isLocalBranch = gitService.hasLocalBranch(build.getBranchName().orElse(""));
        Log.debug("{} is a local git branch: {}", build.getBranchName(), isLocalBranch);
        return !config.getNotificationsConfig().getFilters().localBranchesOnly() || isLocalBranch;
    }

    private boolean buildStatusFilter(JenkinsBuild build) {
        final boolean notSuccess = build.isNotSuccess();
        Log.debug("Jenkins build '{}' is successful: {}", build.getFullDisplayName(), !notSuccess);
        return config.getNotificationsConfig().getFilters().successfulBuilds() || notSuccess;
    }

    private Runnable notifyLastNewFailedBuild(JenkinsNode jenkinsNode) {
        try {
            final Predicate<JenkinsBuildListItem> notCached = build -> {
                final boolean cached = this.isCached(jenkinsNode, build);
                Log.trace("Jenkins build '{}' is already cached: {}", build.getApiUrl(), cached);
                return !cached;
            };

            final Function<JenkinsBuild, JenkinsBuild> cache = build -> {
                Log.trace("Caching jenkins build '{}'", build.getFullDisplayName());
                return this.cache(jenkinsNode, build);
            };

            final long buildCount = jenkinsNode.getBuilds().size();

            if (buildCount > 0) {
                Log.debug("Processing {} builds of Jenkins node '{}'",
                        buildCount, jenkinsNode.getDisplayName());
            } else {
                Log.trace("Jenkins node '{}' has no build to process", jenkinsNode.getDisplayName());
            }

            return () -> jenkinsNode.getBuilds().parallelStream()
                    .filter(notCached)
                    .map(this::fetchBuildDetails)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(JenkinsBuild::hasResult)
                    .map(cache)
                    .min(LATEST_BUILD_FIRST_COMPARATOR)
                    .filter(this::branchFilter)
                    .filter(this::buildStatusFilter)
                    .map(this::notifyUser);
        } catch (Throwable t) {
            Log.error(t, "??");
            return null;
        }
    }

    private Optional<JenkinsBuild> fetchBuildDetails(JenkinsBuildListItem buildListItem) {
        return this.jenkinsApi.getBuild(buildListItem.getApiUrl());
    }

    private static String cachePath(JenkinsNode node, JenkinsBuildNumber jenkinsBuildNumber) {
        return node.getName() + "/" + jenkinsBuildNumber.getNumber();
    }

    private JenkinsBuild cache(JenkinsNode node, JenkinsBuild jenkinsBuild) {
        final String cachePath = cachePath(node, jenkinsBuild);
        this.cacheService.save(cachePath, jenkinsBuild);
        return jenkinsBuild;
    }

    private boolean isCached(JenkinsNode node, JenkinsBuildNumber jenkinsBuildNumber) {
        final String cachePath = cachePath(node, jenkinsBuildNumber);
        return this.cacheService.isCached(cachePath);
    }

    @Override
    public String toString() {
        return "JenkinsBuildStatusNotifier{" +
                "jenkinsApi=" + jenkinsApi +
                '}';
    }
}