package org.varys.gitlab.model.notification;

import org.varys.common.model.NotificationType;
import org.varys.gitlab.model.GitLabMergeRequest;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PendingMergeRequestNotification extends MergeRequestUpdateNotification {

    private final long hoursBeforeReminder;

    PendingMergeRequestNotification(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion, long hoursBeforeReminder) {
        super(mergeRequest, previousVersion);
        this.hoursBeforeReminder = hoursBeforeReminder;
    }

    private static long hoursDiff(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return -1;
        }

        long diffInMillies = date2.getTime() - date1.getTime();
        return TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean shouldNotify() {
        final Date now = Date.from(Instant.now());
        final long hoursSinceLastNotification = hoursDiff(this.getPreviousVersion().getLastNotificationDate(), now);
        return hoursSinceLastNotification >= this.hoursBeforeReminder;
    }

    @Override
    protected String getHeader() {
        return "Merge request is pending";
    }

    @Override
    public NotificationType getType() {
        return NotificationType.WARNING;
    }
}
