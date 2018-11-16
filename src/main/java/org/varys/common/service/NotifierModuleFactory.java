package org.varys.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.varys.common.model.GitConfig;
import org.varys.git.GitService;
import org.varys.gitlab.api.GitLabApiFactory;
import org.varys.gitlab.model.GitLabApiConfig;
import org.varys.gitlab.model.GitLabMergeRequestNotifierConfig;
import org.varys.gitlab.model.GitLabMergeRequestNotifierNotificationsConfig;
import org.varys.gitlab.notifier.GitLabMergeRequestNotifier;
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
        final String trayIconUrl = notificationsConfigNode.get("tray_icon_url").asText();

        final long periodSeconds = notificationsConfigNode.get("period").asLong();

        final JsonNode notificationsFiltersConfigNode = notificationsConfigNode.get("filters");
        final boolean localBranchesOnly = notificationsFiltersConfigNode.get("local_branches_only").asBoolean();
        final boolean successfulBuilds = notificationsFiltersConfigNode.get("successful_builds").asBoolean();
        final JenkinsBuildNotifierNotificationsFiltersConfig filtersConfig =
                new JenkinsBuildNotifierNotificationsFiltersConfig(localBranchesOnly, successfulBuilds);

        final JenkinsBuildNotifierNotificationsConfig notifierNotificationsConfig =
                new JenkinsBuildNotifierNotificationsConfig(periodSeconds, filtersConfig);

        final JenkinsBuildNotifierConfig notifierConfig =
                new JenkinsBuildNotifierConfig(apiConfig, notifierNotificationsConfig);

        return new JenkinsBuildStatusNotifier(
                notifierConfig,
                new GitService(gitConfig),
                new CacheService(moduleName),
                new NotificationService(
                        moduleName,
                        trayIconUrl));
    }

    private static GitLabMergeRequestNotifier createGitLab(JsonNode moduleNode, GitConfig gitConfig) {
        final String moduleName = moduleNode.get("name").asText();
        final JsonNode configNode = moduleNode.get("config");

        final JsonNode apiConfigNode = configNode.get("gitlab_api");
        final int apiVersion = apiConfigNode.get("version").intValue();
        final String apiBaseUrl = apiConfigNode.get("base_url").asText();
        final String apiPrivateToken = apiConfigNode.get("private_token").asText();
        final GitLabApiConfig apiConfig = new GitLabApiConfig(apiVersion, apiBaseUrl, apiPrivateToken);

        final long periodSeconds = configNode.get("notifications").get("period").asLong();
        final GitLabMergeRequestNotifierNotificationsConfig notificationsConfig =
                new GitLabMergeRequestNotifierNotificationsConfig(periodSeconds);

        final GitLabMergeRequestNotifierConfig notifierConfig =
                new GitLabMergeRequestNotifierConfig(apiConfig, notificationsConfig);

        final String trayIconUrl = configNode.get("notifications").get("tray_icon_url").asText();

        return new GitLabMergeRequestNotifier(
                notifierConfig,
                GitLabApiFactory.create(apiConfig),
                new GitService(gitConfig),
                new CacheService(moduleName),
                new NotificationService(
                        moduleName,
                        trayIconUrl));
    }
}