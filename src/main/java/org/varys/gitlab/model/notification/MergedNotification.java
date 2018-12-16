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

public class MergedNotification extends MergeRequestUpdateNotification {

    private final GitLabUser myself;

    MergedNotification(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion, GitLabUser myself) {
        super(mergeRequest, previousVersion);
        this.myself = myself;
    }

    @Override
    public boolean shouldNotify() {
        final GitLabMergeRequest mr = this.getMergeRequest();
        final GitLabMergeRequest previousVersion = this.getPreviousVersion();

        return mr.getAuthor().equals(myself) && mr.isMerged() && !previousVersion.isMerged();
    }

    @Override
    public String getTitle() {
        final String assigneeName = this.getMergeRequest().getOptionalAssignee()
                .map(GitLabUser::getNickname).orElse("Nobody");

        return String.format("Merge request on %s has been merged by %s",
                this.getMergeRequest().getProject().getName(), assigneeName);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
