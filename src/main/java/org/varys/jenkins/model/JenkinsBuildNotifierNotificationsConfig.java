package org.varys.jenkins.model;

public class JenkinsBuildNotifierNotificationsConfig {

    private final long periodSeconds;
    private final JenkinsBuildNotifierNotificationsFiltersConfig filters;

    public JenkinsBuildNotifierNotificationsConfig(
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
        return "JenkinsBuildNotifierNotificationsConfig{" +
                "periodSeconds=" + periodSeconds +
                ", filters=" + filters +
                '}';
    }
}
