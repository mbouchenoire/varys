package org.varys.gitlab.model;

public class GitLabMergeRequestNotificationsFilter {

    private final boolean assignedToMeOnly;

    public GitLabMergeRequestNotificationsFilter(boolean assignedToMeOnly) {
        this.assignedToMeOnly = assignedToMeOnly;
    }

    public boolean isAssignedToMeOnly() {
        return assignedToMeOnly;
    }

    @Override
    public String toString() {
        return "GitLabMergeRequestNotificationsFilter{" +
                "assignedToMeOnly=" + assignedToMeOnly +
                '}';
    }
}
