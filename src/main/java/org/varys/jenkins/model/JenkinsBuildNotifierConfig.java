package org.varys.jenkins.model;

public class JenkinsBuildNotifierConfig {

    private final JenkinsApiConfig jenkinsApiConfig;
    private final JenkinsBuildNotifierNotificationsConfig notificationsConfig;

    public JenkinsBuildNotifierConfig(
            JenkinsApiConfig jenkinsApiConfig, JenkinsBuildNotifierNotificationsConfig notificationsConfig) {
        this.jenkinsApiConfig = jenkinsApiConfig;
        this.notificationsConfig = notificationsConfig;
    }

    public JenkinsApiConfig getJenkinsApiConfig() {
        return jenkinsApiConfig;
    }

    public JenkinsBuildNotifierNotificationsConfig getNotificationsConfig() {
        return notificationsConfig;
    }

    @Override
    public String toString() {
        return "JenkinsBuildNotifierConfig{" +
                "jenkinsApiConfig=" + jenkinsApiConfig +
                ", notificationsConfig=" + notificationsConfig +
                '}';
    }
}
