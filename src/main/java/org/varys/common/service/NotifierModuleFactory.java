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

package org.varys.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.varys.common.model.GitConfig;
import org.varys.git.service.GitService;
import org.varys.gitlab.GitLabNotifier;
import org.varys.gitlab.api.GitLabApiLocator;
import org.varys.gitlab.api.GitLabApiV3;
import org.varys.gitlab.api.GitLabApiV4;
import org.varys.gitlab.model.GitLabApiConfig;
import org.varys.gitlab.model.GitLabNotificationsFilters;
import org.varys.gitlab.model.GitLabNotifierConfig;
import org.varys.gitlab.model.GitLabNotifierNotificationsConfig;
import org.varys.jenkins.JenkinsNotifier;
import org.varys.jenkins.api.JenkinsApi;
import org.varys.jenkins.model.JenkinsApiConfig;
import org.varys.jenkins.model.JenkinsBuildNotifierNotificationsFiltersConfig;
import org.varys.jenkins.model.JenkinsNotifierConfig;
import org.varys.jenkins.model.JenkinsNotifierNotificationsConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.varys.common.service.ConfigFactory.*;

public class NotifierModuleFactory {

    private static final String EMPTY_STRING = "<empty>";

    private static final boolean DEFAULT_SSL_VERIFY = true;
    private static final int DEFAULT_NOTIFICATION_PERIOD = 30;

    private static final boolean DEFAULT_LOCAL_BRANCH_ONLY_FILTER = true;
    private static final int DEFAULT_HOURS_BEFORE_REMINDER = 24;

    private static final boolean DEFAULT_SUCCESSFUL_BUILD_FILTER = false;
    private static final boolean DEFAULT_ASSIGNED_TO_ME_ONLY_FILTER = true;

    private final GitConfig gitConfig;
    private final GitLabApiLocator gitLabApiLocator;
    private final Map<String, BiFunction<JsonNode, GitConfig, NotifierModule>> moduleFactories;

    public NotifierModuleFactory(GitConfig gitConfig, GitLabApiLocator gitLabApiLocator) {
        this.gitConfig = gitConfig;
        this.gitLabApiLocator = gitLabApiLocator;
        this.moduleFactories = new HashMap<>();
        this.moduleFactories.put("jenkins", this::createJenkins);
        this.moduleFactories.put("gitlab", this::createGitLab);
    }

    public Collection<NotifierModule> createAll(Collection<JsonNode> moduleNodes) {
        return moduleNodes.stream()
                .map(this::create)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<NotifierModule> create(JsonNode moduleRootNode) {
        final String moduleName = moduleRootNode.fieldNames().next();

        Log.debug("Found module with name '{}'...", moduleName);

        final JsonNode moduleNode = moduleRootNode.get(moduleName);

        final boolean enabled = moduleNode.get("enabled").asBoolean(true);

        if (!enabled) {
            Log.warn("{} module is not enabled", moduleName);
            return Optional.empty();
        }

        final BiFunction<JsonNode, GitConfig, NotifierModule> moduleFactory = this.moduleFactories.get(moduleName);

        if (moduleFactory == null) {
            Log.error("Cannot find module with name '{}'", moduleName);
            return Optional.empty();
        }

        final NotifierModule module = moduleFactory.apply(moduleNode, this.gitConfig);

        Log.info("Initialized module: {}", module);

        return Optional.of(module);
    }

    private JenkinsNotifier createJenkins(JsonNode moduleNode, GitConfig gitConfig) {
        final String moduleName = getString(moduleNode, "name", "jenkins");

        final String apiBaseUrl = getString(moduleNode, "config.jenkins_api.base_url", EMPTY_STRING);
        final String apiToken = getString(moduleNode, "config.jenkins_api.api_token", EMPTY_STRING);
        final boolean sslVerify = getBoolean(moduleNode, "config.jenkins_api.ssl_verify", DEFAULT_SSL_VERIFY);
        final JenkinsApiConfig apiConfig = new JenkinsApiConfig(apiBaseUrl, apiToken, sslVerify);

        final long periodSeconds = getLong(moduleNode, "config.notifications.period", DEFAULT_NOTIFICATION_PERIOD);

        final boolean localBranchesOnly = getBoolean(
                moduleNode,"config.notifications.filters.local_branches_only", DEFAULT_LOCAL_BRANCH_ONLY_FILTER);

        final boolean successfulBuilds = getBoolean(
                moduleNode, "config.notifications.filters.successful_builds", DEFAULT_SUCCESSFUL_BUILD_FILTER);

        final JenkinsBuildNotifierNotificationsFiltersConfig filtersConfig =
                new JenkinsBuildNotifierNotificationsFiltersConfig(localBranchesOnly, successfulBuilds);

        final JenkinsNotifierNotificationsConfig notifierNotificationsConfig =
                new JenkinsNotifierNotificationsConfig(periodSeconds, filtersConfig);

        final JenkinsNotifierConfig notifierConfig = new JenkinsNotifierConfig(notifierNotificationsConfig);

        final JenkinsApi jenkinsApi = new JenkinsApi(apiConfig);

        return new JenkinsNotifier(
                jenkinsApi,
                notifierConfig,
                new GitService(gitConfig),
                new CacheService(moduleName),
                new NotificationService(moduleName));
    }

    private GitLabNotifier createGitLab(JsonNode moduleNode, GitConfig gitConfig) {
        Log.trace("Unused git config for GitLab module: {}", gitConfig);

        final String moduleName = getString(moduleNode, "name", "gitlab");

        final String apiBaseUrl = getString(moduleNode, "config.gitlab_api.base_url", EMPTY_STRING);
        final String apiPrivateToken = getString(moduleNode, "config.gitlab_api.private_token", EMPTY_STRING);
        final boolean sslVerify = getBoolean(moduleNode, "config.gitlab_api.ssl_verify", DEFAULT_SSL_VERIFY);
        final GitLabApiConfig apiConfig = new GitLabApiConfig(apiBaseUrl, apiPrivateToken, sslVerify);

        final long periodSeconds = getLong(moduleNode, "config.notifications.period", DEFAULT_NOTIFICATION_PERIOD);

        final boolean assignedToMeOnly = getBoolean(
                moduleNode, "config.notifications.filters.assigned_to_me_only", DEFAULT_ASSIGNED_TO_ME_ONLY_FILTER);

        final long hoursBeforeReminder = getLong(
                moduleNode, "config.notifications.filters.hours_before_reminder", DEFAULT_HOURS_BEFORE_REMINDER);

        final GitLabNotificationsFilters notificationsFilter = new GitLabNotificationsFilters(assignedToMeOnly, hoursBeforeReminder);

        final GitLabNotifierNotificationsConfig notificationsConfig =
                new GitLabNotifierNotificationsConfig(periodSeconds, notificationsFilter);

        final GitLabNotifierConfig notifierConfig = new GitLabNotifierConfig(notificationsConfig);

        final GitLabApiV3 gitLabApiV3 = new GitLabApiV3(apiConfig);
        final GitLabApiV4 gitLabApiV4 = new GitLabApiV4(apiConfig);

        return new GitLabNotifier(
                notifierConfig,
                this.gitLabApiLocator.findUsable(gitLabApiV3, gitLabApiV4),
                new CacheService(moduleName),
                new NotificationService(moduleName));
    }
}
