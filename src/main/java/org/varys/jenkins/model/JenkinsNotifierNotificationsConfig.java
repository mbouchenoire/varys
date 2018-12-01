package org.varys.jenkins.model;

public class JenkinsNotifierNotificationsConfig {

    private final long periodSeconds;
    private final JenkinsBuildNotifierNotificationsFiltersConfig filters;

    public JenkinsNotifierNotificationsConfig(
            long periodSeconds,
            JenkinsBuildNotifierNotificationsFiltersConfig filters) {

        this.periodSeconds = periodSeconds;
        this.filters = filters;
    }

    public long getPeriodSeconds() {
        return periodSeconds;
    }

    public JenkinsBuildNotifierNotificationsFiltersConfig getFilters() {
        return filters;
    }

    @Override
    public String toString() {
        return "JenkinsNotifierNotificationsConfig{" +
                "periodSeconds=" + periodSeconds +
                ", filters=" + filters +
                '}';
    }
}
