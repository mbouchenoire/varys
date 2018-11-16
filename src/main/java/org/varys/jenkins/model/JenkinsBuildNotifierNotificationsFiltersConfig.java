package org.varys.jenkins.model;

public class JenkinsBuildNotifierNotificationsFiltersConfig {

    private final boolean localBranchesOnly;
    private final boolean successfulBuilds;

    public JenkinsBuildNotifierNotificationsFiltersConfig(boolean localBranchesOnly, boolean successfulBuilds) {
        this.localBranchesOnly = localBranchesOnly;
        this.successfulBuilds = successfulBuilds;
    }

    public boolean localBranchesOnly() {
        return localBranchesOnly;
    }

    public boolean successfulBuilds() {
        return successfulBuilds;
    }

    @Override
    public String toString() {
        return "JenkinsBuildNotifierNotificationsFiltersConfig{" +
                "localBranchesOnly=" + localBranchesOnly +
                ", successfulBuilds=" + successfulBuilds +
                '}';
    }
}
