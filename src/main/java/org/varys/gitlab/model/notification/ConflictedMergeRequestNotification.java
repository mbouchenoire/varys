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

public class ConflictedMergeRequestNotification extends MergeRequestUpdateNotification {

    private GitLabUser myself;

    ConflictedMergeRequestNotification(
            GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion, GitLabUser myself) {

        super(mergeRequest, previousVersion);
        this.myself = myself;
    }

    @Override
    public boolean shouldNotify() {
        final GitLabMergeRequest mr = this.getMergeRequest();
        final GitLabMergeRequest previousVersion = this.getPreviousVersion();

        if (mr.hasConflict() == previousVersion.hasConflict()) {
            return false;
        }

        if (mr.hasConflict()) {
            return mr.getAuthor().equals(myself);
        } else {
            return mr.getAssignee().equals(myself);
        }
    }

    @Override
    public String getTitle() {
        final GitLabMergeRequest mr = this.getMergeRequest();

        final String firstLine = mr.hasConflict()
                ? "Conflicted merge request on " + mr.getProject().getName()
                : "Merge request no longer conflicted";

        return firstLine + "\n" + this.getMergeRequest().getIdentifier();
    }

    @Override
    public NotificationType getType() {
        return this.getMergeRequest().hasConflict()
                ? NotificationType.WARNING
                : NotificationType.INFO;
    }
}
