package org.varys.jenkins.model;

public class JenkinsBuildNotifierConfig {

    private final JenkinsBuildNotifierNotificationsConfig notificationsConfig;

    public JenkinsBuildNotifierConfig(JenkinsBuildNotifierNotificationsConfig notificationsConfig) {
        this.notificationsConfig = notificationsConfig;
    }

    public JenkinsBuildNotifierNotificationsConfig getNotificationsConfig() {
        return notificationsConfig;
    }

    @Override
    public String toString() {
        return "JenkinsBuildNotifierConfig{" +
                ", notificationsConfig=" + notificationsConfig +
                '}';
    }
}
