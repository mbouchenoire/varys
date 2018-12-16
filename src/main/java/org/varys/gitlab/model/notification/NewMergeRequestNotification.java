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

public class NewMergeRequestNotification extends MergeRequestNotification {

    private final GitLabMergeRequest mergeRequest;

    public NewMergeRequestNotification(GitLabMergeRequest mergeRequest) {
        super(mergeRequest);
        this.mergeRequest = mergeRequest;
    }

    @Override
    public String getTitle() {
        final String authorName = mergeRequest.getAuthor().getNickname();

        final String assigneeName = mergeRequest.getOptionalAssignee()
                .map(GitLabUser::getNickname).orElse("Nobody");

        return "New merge request on " + this.mergeRequest.getProject().getName() + "\n"
                + "By " + authorName + " for " + assigneeName;
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
