package org.varys.gitlab.model;

public class GitLabNotifierNotificationsConfig {

    private final long periodSeconds;
    private final GitLabNotificationsFilters notificationsFilter;

    public GitLabNotifierNotificationsConfig(
            long periodSeconds, GitLabNotificationsFilters notificationsFilter) {

        this.periodSeconds = periodSeconds;
        this.notificationsFilter = notificationsFilter;
    }

    public long getPeriodSeconds() {
        return periodSeconds;
    }

    public GitLabNotificationsFilters getFilters() {
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
