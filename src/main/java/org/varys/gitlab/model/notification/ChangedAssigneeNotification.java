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

class ChangedAssigneeNotification extends MergeRequestUpdateNotification {

    ChangedAssigneeNotification(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion) {
        super(mergeRequest, previousVersion);
    }

    @Override
    public boolean shouldNotify() {
        return !this.getMergeRequest().sameAssignee(this.getPreviousVersion());
    }

    @Override
    public String getTitle() {
        final String previousAssignee = this.getPreviousVersion().getOptionalAssignee()
                .map(GitLabUser::getNickname).orElse("Nobody");

        final String newAssignee = this.getMergeRequest().getOptionalAssignee()
                .map(assignee -> assignee.getName().split(" ")[0]).orElse("Nobody");

        return "Merge request changed assignee\n"
                + previousAssignee + " -> " + newAssignee;
    }

    @Override
    public NotificationType getType() {
        return NotificationType.WARNING;
    }
}
