package org.varys.gitlab.model;

public class GitLabMergeRequestNotifierNotificationsConfig {

    private final long periodSeconds;

    public GitLabMergeRequestNotifierNotificationsConfig(long periodSeconds) {
        this.periodSeconds = periodSeconds;
    }

    public long getPeriodSeconds() {
        return periodSeconds;
    }

    @Override
    public String toString() {
        return "GitLabMergeRequestNotifierNotificationsConfig{" +
                "periodSeconds=" + periodSeconds +
                '}';
    }
}
