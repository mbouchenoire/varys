package org.varys.gitlab.model;

public class GitLabNotificationsFilters {

    private final boolean involvingMyselfOnly;
    private final long hoursBeforeReminder;

    public GitLabNotificationsFilters(boolean involvingMyselfOnly, long hoursBeforeReminder) {
        this.involvingMyselfOnly = involvingMyselfOnly;
        this.hoursBeforeReminder = hoursBeforeReminder;
    }

    public boolean isInvolvingMyselfOnly() {
        return involvingMyselfOnly;
    }

    public long getHoursBeforeReminder() {
        return hoursBeforeReminder;
    }

    @Override
    public String toString() {
        return "GitLabNotificationsFilters{" +
                "involvingMyselfOnly=" + involvingMyselfOnly +
                ", hoursBeforeReminder=" + hoursBeforeReminder +
                '}';
    }
}
