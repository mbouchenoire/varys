/*
 * This file is part of Varys.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Varys.  If not, see <https://www.gnu.org/licenses/>.
 */

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
        return !this.getMergeRequest().isWip() && hoursSinceLastNotification >= this.hoursBeforeReminder;
    }

    @Override
    public String getTitle() {
        return "Merge request is pending\n" + this.getMergeRequest().getIdentifier();
    }

    @Override
    public NotificationType getType() {
        return NotificationType.WARNING;
    }
}
