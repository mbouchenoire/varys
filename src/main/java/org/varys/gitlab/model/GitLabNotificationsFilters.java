package org.varys.gitlab.model;

public class GitLabNotificationsFilters {

    private final boolean assignedToMeOnly;
    private final long hoursBeforeReminder;

    public GitLabNotificationsFilters(boolean assignedToMeOnly, long hoursBeforeReminder) {
        this.assignedToMeOnly = assignedToMeOnly;
        this.hoursBeforeReminder = hoursBeforeReminder;
    }

    public boolean isAssignedToMeOnly() {
        return assignedToMeOnly;
    }

    public long getHoursBeforeReminder() {
        return hoursBeforeReminder;
    }

    @Override
    public String toString() {
        return "GitLabNotificationsFilters{" +
                "assignedToMeOnly=" + assignedToMeOnly +
                ", hoursBeforeReminder=" + hoursBeforeReminder +
                '}';
    }
}
