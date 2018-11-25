package org.varys.gitlab.model;

public class GitLabNotificationsFilter {

    private final boolean assignedToMeOnly;

    public GitLabNotificationsFilter(boolean assignedToMeOnly) {
        this.assignedToMeOnly = assignedToMeOnly;
    }

    public boolean isAssignedToMeOnly() {
        return assignedToMeOnly;
    }

    @Override
    public String toString() {
        return "GitLabNotificationsFilter{" +
                "assignedToMeOnly=" + assignedToMeOnly +
                '}';
    }
}
