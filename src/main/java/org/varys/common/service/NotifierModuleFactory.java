package org.varys.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.varys.common.model.GitConfig;
import org.varys.git.GitService;
import org.varys.gitlab.api.GitLabApiFactory;
import org.varys.gitlab.model.GitLabApiConfig;
import org.varys.gitlab.model.GitLabNotificationsFilters;
import org.varys.gitlab.model.GitLabNotifierConfig;
import org.varys.gitlab.model.GitLabNotifierNotificationsConfig;
import org.varys.gitlab.notifier.GitLabNotifier;
import org.varys.jenkins.api.JenkinsApi;
import org.varys.jenkins.model.JenkinsApiConfig;
import org.varys.jenkins.model.JenkinsBuildNotifierConfig;
import org.varys.jenkins.model.JenkinsBuildNotifierNotificationsConfig;
import org.varys.jenkins.model.JenkinsBuildNotifierNotificationsFiltersConfig;
import org.varys.jenkins.notifier.JenkinsBuildStatusNotifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class NotifierModuleFactory {

    private static final int DEFAULT_HOURS_BEFORE_REMINDER = 24;

    private final GitConfig gitConfig;
    private final Map<String, BiFunction<JsonNode, GitConfig, NotifierModule>> moduleFactories;

    public NotifierModuleFactory(GitConfig gitConfig) {
        this.gitConfig = gitConfig;
        this.moduleFactories = new HashMap<>();
        this.moduleFactories.put("jenkins", NotifierModuleFactory::createJenkins);
        this.moduleFactories.put("gitlab", NotifierModuleFactory::createGitLab);
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

    private static JenkinsBuildStatusNotifier createJenkins(JsonNode moduleNode, GitConfig gitConfig) {
        final String moduleName = moduleNode.get("name").asText();
        final JsonNode configNode = moduleNode.get("config");

        final JsonNode jenkinsApiConfigNode = configNode.get("jenkins_api");
        final String apiBaseUrl = jenkinsApiConfigNode.get("base_url").asText();
        final String apiToken = jenkinsApiConfigNode.get("api_token").asText();
        final JenkinsApiConfig apiConfig = new JenkinsApiConfig(apiBaseUrl, apiToken);

        final JsonNode notificationsConfigNode = configNode.get("notifications");

        final long periodSeconds = notificationsConfigNode.get("period").asLong();

        final JsonNode notificationsFiltersConfigNode = notificationsConfigNode.get("filters");
        final boolean localBranchesOnly = notificationsFiltersConfigNode.get("local_branches_only").asBoolean();
        final boolean successfulBuilds = notificationsFiltersConfigNode.get("successful_builds").asBoolean();
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

    private static GitLabNotifier createGitLab(JsonNode moduleNode, GitConfig gitConfig) {
        Log.trace("Unused git config for GitLab module: {}", gitConfig);

        final String moduleName = moduleNode.get("name").asText();
        final JsonNode configNode = moduleNode.get("config");

        final JsonNode apiConfigNode = configNode.get("gitlab_api");
        final int apiVersion = apiConfigNode.get("version").intValue();
        final String apiBaseUrl = apiConfigNode.get("base_url").asText();
        final String apiPrivateToken = apiConfigNode.get("private_token").asText();
        final GitLabApiConfig apiConfig = new GitLabApiConfig(apiVersion, apiBaseUrl, apiPrivateToken);

        final JsonNode notificationsNode = configNode.get("notifications");

        final long periodSeconds = notificationsNode.get("period").asLong();

        final JsonNode filtersNode = notificationsNode.get("filters");
        final boolean assignedToMeOnly = filtersNode.get("assigned_to_me_only").asBoolean(true);
        final long hoursBeforeReminder = filtersNode.get("hours_before_reminder").asLong(DEFAULT_HOURS_BEFORE_REMINDER);
        final GitLabNotificationsFilters notificationsFilter =
                new GitLabNotificationsFilters(assignedToMeOnly, hoursBeforeReminder);

        final GitLabNotifierNotificationsConfig notificationsConfig =
                new GitLabNotifierNotificationsConfig(periodSeconds, notificationsFilter);

        final GitLabNotifierConfig notifierConfig = new GitLabNotifierConfig(notificationsConfig);

        return new GitLabNotifier(
                notifierConfig,
                GitLabApiFactory.create(apiConfig),
                new CacheService(moduleName),
                new NotificationService(moduleName));
    }
}
