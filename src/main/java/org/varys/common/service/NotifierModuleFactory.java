package org.varys.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.varys.common.model.GitConfig;
import org.varys.git.service.GitService;
import org.varys.gitlab.GitLabNotifier;
import org.varys.gitlab.api.GitLabApiFactory;
import org.varys.gitlab.model.GitLabApiConfig;
import org.varys.gitlab.model.GitLabNotificationsFilters;
import org.varys.gitlab.model.GitLabNotifierConfig;
import org.varys.gitlab.model.GitLabNotifierNotificationsConfig;
import org.varys.jenkins.JenkinsBuildStatusNotifier;
import org.varys.jenkins.api.JenkinsApi;
import org.varys.jenkins.model.JenkinsApiConfig;
import org.varys.jenkins.model.JenkinsBuildNotifierConfig;
import org.varys.jenkins.model.JenkinsBuildNotifierNotificationsConfig;
import org.varys.jenkins.model.JenkinsBuildNotifierNotificationsFiltersConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.varys.common.service.ConfigFactory.*;

public class NotifierModuleFactory {

    private static final String EMPTY_STRING = "<empty>";

    private static final int DEFAULT_NOTIFICATION_PERIOD = 30;

    private static final boolean DEFAULT_LOCAL_BRANCH_ONLY_FILTER = true;
    private static final int DEFAULT_HOURS_BEFORE_REMINDER = 24;

    private static final boolean DEFAULT_SUCCESSFUL_BUILD_FILTER = false;
    private static final boolean DEFAULT_ASSIGNED_TO_ME_ONLY_FILTER = true;

    private final GitConfig gitConfig;
    private final GitLabApiFactory gitLabApiFactory;
    private final Map<String, BiFunction<JsonNode, GitConfig, NotifierModule>> moduleFactories;

    public NotifierModuleFactory(GitConfig gitConfig, GitLabApiFactory gitLabApiFactory) {
        this.gitConfig = gitConfig;
        this.gitLabApiFactory = gitLabApiFactory;
        this.moduleFactories = new HashMap<>();
        this.moduleFactories.put("jenkins", this::createJenkins);
        this.moduleFactories.put("gitlab", this::createGitLab);
    }

    private boolean isEnabled(JsonNode moduleNode) {
        final boolean enabled = moduleNode.get("enabled").asBoolean();

        if (!enabled) {
            final String name = moduleNode.get("name").asText();
            Log.warn("{} module is not enabled", name);
        }

        return enabled;
    }

    public Collection<NotifierModule> createAll(Collection<JsonNode> moduleNodes) {
        return moduleNodes.stream()
                .filter(this::isEnabled)
                .map(this::create)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<NotifierModule> create(JsonNode moduleNode) {
        final String moduleName = moduleNode.get("name").asText();

        Log.debug("Initializing module with name '{}'...", moduleName);

        final BiFunction<JsonNode, GitConfig, NotifierModule> moduleFactory = this.moduleFactories.get(moduleName);

        if (moduleFactory == null) {
            Log.error("Cannot find module with name '{}'", moduleName);
            return Optional.empty();
        }

        final NotifierModule module = moduleFactory.apply(moduleNode, this.gitConfig);

        Log.info("Initialized module: {}", module);

        return Optional.of(module);
    }

    private JenkinsBuildStatusNotifier createJenkins(JsonNode moduleNode, GitConfig gitConfig) {
        final String moduleName = getString(moduleNode, "name", "jenkins");

        final String apiBaseUrl = getString(moduleNode, "config.jenkins_api.base_url", EMPTY_STRING);
        final String apiToken = getString(moduleNode, "config.jenkins_api.api_token", EMPTY_STRING);
        final JenkinsApiConfig apiConfig = new JenkinsApiConfig(apiBaseUrl, apiToken);

        final long periodSeconds = getLong(moduleNode, "config.notifications.period", DEFAULT_NOTIFICATION_PERIOD);

        final boolean localBranchesOnly = getBoolean(
                moduleNode,"config.notifications.filters.local_branches_only", DEFAULT_LOCAL_BRANCH_ONLY_FILTER);

        final boolean successfulBuilds = getBoolean(
                moduleNode, "config.notifications.filters.successful_builds", DEFAULT_SUCCESSFUL_BUILD_FILTER);

        final JenkinsBuildNotifierNotificationsFiltersConfig filtersConfig =
                new JenkinsBuildNotifierNotificationsFiltersConfig(localBranchesOnly, successfulBuilds);

        final JenkinsBuildNotifierNotificationsConfig notifierNotificationsConfig =
                new JenkinsBuildNotifierNotificationsConfig(periodSeconds, filtersConfig);

        final JenkinsBuildNotifierConfig notifierConfig = new JenkinsBuildNotifierConfig(notifierNotificationsConfig);

        final JenkinsApi jenkinsApi = new JenkinsApi(apiConfig);

        return new JenkinsBuildStatusNotifier(
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
        final GitLabApiConfig apiConfig = new GitLabApiConfig(apiBaseUrl, apiPrivateToken);

        final long periodSeconds = getLong(moduleNode, "config.notifications.period", DEFAULT_NOTIFICATION_PERIOD);

        final boolean assignedToMeOnly = getBoolean(
                moduleNode, "config.notifications.filters.assigned_to_me_only", DEFAULT_ASSIGNED_TO_ME_ONLY_FILTER);

        final long hoursBeforeReminder = getLong(
                moduleNode, "config.notifications.filters.hours_before_reminder", DEFAULT_HOURS_BEFORE_REMINDER);

        final GitLabNotificationsFilters notificationsFilter = new GitLabNotificationsFilters(assignedToMeOnly, hoursBeforeReminder);

        final GitLabNotifierNotificationsConfig notificationsConfig =
                new GitLabNotifierNotificationsConfig(periodSeconds, notificationsFilter);

        final GitLabNotifierConfig notifierConfig = new GitLabNotifierConfig(notificationsConfig);

        return new GitLabNotifier(
                notifierConfig,
                this.gitLabApiFactory.create(apiConfig),
                new CacheService(moduleName),
                new NotificationService(moduleName));
    }
}
