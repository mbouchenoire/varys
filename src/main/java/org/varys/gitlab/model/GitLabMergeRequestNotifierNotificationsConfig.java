package org.varys.gitlab.model;

public class GitLabMergeRequestNotifierNotificationsConfig {

    private final long periodSeconds;
    private final GitLabMergeRequestNotificationsFilter notificationsFilter;

    public GitLabMergeRequestNotifierNotificationsConfig(
            long periodSeconds, GitLabMergeRequestNotificationsFilter notificationsFilter) {

        this.periodSeconds = periodSeconds;
        this.notificationsFilter = notificationsFilter;
    }

    public long getPeriodSeconds() {
        return periodSeconds;
    }

    public GitLabMergeRequestNotificationsFilter getNotificationsFilter() {
        return notificationsFilter;
    }

    @Override
    public String toString() {
        return "GitLabMergeRequestNotifierNotificationsConfig{" +
                "periodSeconds=" + periodSeconds +
                ", notificationsFilter=" + notificationsFilter +
                '}';
    }
}
