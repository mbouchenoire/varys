package org.varys.gitlab.model;

public class GitLabMergeRequestNotifierConfig {

    private final GitLabApiConfig apiConfig;
    private final GitLabMergeRequestNotifierNotificationsConfig notificationsConfig;

    public GitLabMergeRequestNotifierConfig(
            GitLabApiConfig apiConfig, GitLabMergeRequestNotifierNotificationsConfig notificationsConfig) {

        this.apiConfig = apiConfig;
        this.notificationsConfig = notificationsConfig;
    }

    public GitLabApiConfig getApiConfig() {
        return apiConfig;
    }

    public GitLabMergeRequestNotifierNotificationsConfig getNotificationsConfig() {
        return notificationsConfig;
    }

    @Override
    public String toString() {
        return "GitLabNotifierConfig{" +
                "apiConfig=" + apiConfig +
                '}';
    }
}
