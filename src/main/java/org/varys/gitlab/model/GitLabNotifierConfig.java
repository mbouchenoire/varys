package org.varys.gitlab.model;

public class GitLabNotifierConfig {

    private final GitLabNotifierNotificationsConfig notificationsConfig;

    public GitLabNotifierConfig(GitLabNotifierNotificationsConfig notificationsConfig) {

        this.notificationsConfig = notificationsConfig;
    }

    public GitLabNotifierNotificationsConfig getNotificationsConfig() {
        return notificationsConfig;
    }

    @Override
    public String toString() {
        return "GitLabNotifierConfig{" +
                "notificationsConfig=" + notificationsConfig +
                '}';
    }
}
