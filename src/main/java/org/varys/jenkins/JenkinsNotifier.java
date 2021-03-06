/*
 * This file is part of Varys.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Varys.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.varys.jenkins;

import org.pmw.tinylog.Logger;
import org.varys.common.service.CacheService;
import org.varys.common.service.NotificationService;
import org.varys.common.service.NotifierModule;
import org.varys.common.service.RestApiService;
import org.varys.git.service.GitService;
import org.varys.jenkins.api.JenkinsApi;
import org.varys.jenkins.model.JenkinsBuild;
import org.varys.jenkins.model.JenkinsBuildListItem;
import org.varys.jenkins.model.JenkinsBuildNumber;
import org.varys.jenkins.model.JenkinsNode;
import org.varys.jenkins.model.JenkinsNotifierConfig;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class JenkinsNotifier implements NotifierModule {

    private static final Comparator<JenkinsBuildNumber> LATEST_BUILD_FIRST_COMPARATOR =
            (build1, build2) -> Long.compare(build2.getNumber(), build1.getNumber());

    private final JenkinsApi jenkinsApi;
    private final JenkinsNotifierConfig config;
    private final RestApiService restApiService;
    private final GitService gitService;
    private final CacheService cacheService;
    private final NotificationService notificationService;

    public JenkinsNotifier(
            JenkinsApi jenkinsApi,
            JenkinsNotifierConfig notifierConfig,
            GitService gitService,
            CacheService cacheService,
            NotificationService notificationService) {

        this.jenkinsApi = jenkinsApi;
        this.config = notifierConfig;
        this.restApiService = new RestApiService(cacheService, notificationService);
        this.gitService = gitService;
        this.cacheService = cacheService;
        this.notificationService = notificationService;
    }

    @Override
    public String getName() {
        return "Jenkins";
    }

    @Override
    public long getPeriodSeconds() {
        return this.config.getNotificationsConfig().getPeriodSeconds();
    }

    @Override
    public void iterate() {
        if (this.restApiService.isOffline()) {
            Logger.warn("No internet access, aborting Jenkins notification iteration");
            return;
        }

        final boolean apiIsOnline = this.restApiService.notifyApiStatus(this.jenkinsApi);

        if (apiIsOnline) {
            this.jenkinsApi.getRootNode().ifPresent(this::notifyUser);
        } else {
            Logger.error("Jenkins API is down");
        }
    }

    private void notifyUser(JenkinsNode jenkinsNode) {
        final Runnable traverseJenkinsNode = traverseJenkinsNode(jenkinsNode);
        final Runnable notifyLastNewFailedBuild = notifyLastNewFailedBuild(jenkinsNode);

        Arrays.asList(traverseJenkinsNode, notifyLastNewFailedBuild).parallelStream()
                .forEach(Runnable::run);
    }

    private Runnable traverseJenkinsNode(JenkinsNode jenkinsNode) {
        final long childrenCount = jenkinsNode.getChildren().size();

        if (childrenCount > 0) {
            Logger.debug("Processing {} children of Jenkins node '{}'",
                    jenkinsNode.getChildren().size(), jenkinsNode.getDisplayName());
        } else {
            Logger.trace("Jenkins node '{}' has no child to process", jenkinsNode.getDisplayName());
        }

        return () -> jenkinsNode.getChildren().parallelStream()
                .forEach(child -> this.jenkinsApi.getNode(child.getApiUrl()).ifPresent(this::notifyUser));
    }

    private boolean branchFilter(JenkinsBuild build) {
        final boolean isLocalBranch = gitService.hasLocalBranch(build.getBranchName().orElse(""));
        Logger.debug("{} is a local git branch: {}", build.getBranchName(), isLocalBranch);
        return !config.getNotificationsConfig().getFilters().localBranchesOnly() || isLocalBranch;
    }

    private boolean buildStatusFilter(JenkinsBuild build) {
        final boolean notSuccess = build.isNotSuccess();
        Logger.debug("Jenkins build '{}' is successful: {}", build.getFullDisplayName(), !notSuccess);
        return config.getNotificationsConfig().getFilters().successfulBuilds() || notSuccess;
    }

    private Runnable notifyLastNewFailedBuild(JenkinsNode jenkinsNode) {
        final Predicate<JenkinsBuildListItem> notCached = build -> {
            final boolean cached = this.isCached(jenkinsNode, build);
            Logger.trace("Jenkins build '{}' is already cached: {}", build.getApiUrl(), cached);
            return !cached;
        };

        final Function<JenkinsBuild, JenkinsBuild> cache = build -> {
            Logger.trace("Caching jenkins build '{}'", build.getFullDisplayName());
            return this.cache(jenkinsNode, build);
        };

        final long buildCount = jenkinsNode.getBuilds().size();

        if (buildCount > 0) {
            Logger.debug("Processing {} builds of Jenkins node '{}'",
                    buildCount, jenkinsNode.getDisplayName());
        } else {
            Logger.trace("Jenkins node '{}' has no build to process", jenkinsNode.getDisplayName());
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
                .map(jenkinsBuild -> {
                    this.notificationService.send(jenkinsBuild);
                    return jenkinsBuild;
                });
    }

    private Optional<JenkinsBuild> fetchBuildDetails(JenkinsBuildListItem buildListItem) {
        return this.jenkinsApi.getBuild(buildListItem.getApiUrl());
    }

    private static String cachePath(JenkinsNode node, JenkinsBuildNumber jenkinsBuildNumber) {
        return node.getName() + File.separator + jenkinsBuildNumber.getNumber();
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
        return "JenkinsNotifier{" +
                "jenkinsApi=" + jenkinsApi +
                '}';
    }
}
