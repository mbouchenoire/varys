package org.varys.jenkins.model;

public class JenkinsNotifierConfig {

    private final JenkinsNotifierNotificationsConfig notificationsConfig;

    public JenkinsNotifierConfig(JenkinsNotifierNotificationsConfig notificationsConfig) {
        this.notificationsConfig = notificationsConfig;
    }

    public JenkinsNotifierNotificationsConfig getNotificationsConfig() {
        return notificationsConfig;
    }

    @Override
    public String toString() {
        return "JenkinsNotifierConfig{" +
                ", notificationsConfig=" + notificationsConfig +
                '}';
    }
}
