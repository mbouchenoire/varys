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

class NewCommitsNotification extends MergeRequestUpdateNotification {

    private final GitLabUser myself;

    NewCommitsNotification(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion, GitLabUser myself) {
        super(mergeRequest, previousVersion);
        this.myself = myself;
    }

    @Override
    public boolean shouldNotify() {
        final GitLabMergeRequest mr = this.getMergeRequest();
        final GitLabMergeRequest previousVersion = this.getPreviousVersion();

        return mr.getOptionalAssignee()
                .map(assignee -> assignee.equals(myself) && !mr.isWip() && mr.addedCommitsCount(previousVersion) > 0)
                .orElse(false);
    }

    @Override
    public String getTitle() {
        final GitLabMergeRequest mr = this.getMergeRequest();
        final long addedCommitsCount = mr.addedCommitsCount(this.getPreviousVersion());
        final String s = addedCommitsCount > 1 ? "s" : "";
        return String.format("%d new commit%s on merge request\n%s", addedCommitsCount, s, mr.getIdentifier());
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
