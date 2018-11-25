package org.varys.gitlab.model;

public class GitLabNotifierNotificationsConfig {

    private final long periodSeconds;
    private final GitLabNotificationsFilter notificationsFilter;

    public GitLabNotifierNotificationsConfig(
            long periodSeconds, GitLabNotificationsFilter notificationsFilter) {

        this.periodSeconds = periodSeconds;
        this.notificationsFilter = notificationsFilter;
    }

    public long getPeriodSeconds() {
        return periodSeconds;
    }

    public GitLabNotificationsFilter getNotificationsFilter() {
        return notificationsFilter;
    }

    @Override
    public String toString() {
        return "GitLabNotifierNotificationsConfig{" +
                "periodSeconds=" + periodSeconds +
                ", notificationsFilter=" + notificationsFilter +
                '}';
    }
}
