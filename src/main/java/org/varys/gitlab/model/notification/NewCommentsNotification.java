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
import org.varys.gitlab.model.GitLabUser;

class NewCommentsNotification extends MergeRequestUpdateNotification {

    private final GitLabUser myself;

    NewCommentsNotification(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion, GitLabUser myself) {
        super(mergeRequest, previousVersion);
        this.myself = myself;
    }

    @Override
    public boolean shouldNotify() {
        final GitLabMergeRequest mr = this.getMergeRequest();
        final GitLabMergeRequest previousVersion = this.getPreviousVersion();

        return mr.getAuthor().equals(myself) && mr.addedUserNotesCount(previousVersion) > 0;
    }

    @Override
    public String getTitle() {
        final GitLabMergeRequest mr = this.getMergeRequest();
        final long addedUserNotesCount = mr.addedUserNotesCount(this.getPreviousVersion());
        final String s = addedUserNotesCount > 1 ? "s" : "";
        return String.format("%d new comment%s on merge request\n%s", addedUserNotesCount, s, mr.getIdentifier());
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
