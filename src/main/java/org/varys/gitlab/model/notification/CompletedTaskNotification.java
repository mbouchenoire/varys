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
import org.varys.gitlab.model.GitLabMergeRequestTask;
import org.varys.gitlab.model.GitLabUser;

class CompletedTaskNotification extends MergeRequestUpdateNotification {

    private final GitLabUser myself;

    CompletedTaskNotification(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion, GitLabUser myself) {
        super(mergeRequest, previousVersion);
        this.myself = myself;
    }

    @Override
    public boolean shouldNotify() {
        final GitLabMergeRequest mr = this.getMergeRequest();
        final GitLabMergeRequest previousVersion = this.getPreviousVersion();

        final boolean anyTaskNewlyCompletedNotByMyself = mr.getTasks().stream()
                .filter(GitLabMergeRequestTask::isCompleted)
                .filter(freshTask -> freshTask.getCompletor().map(completor -> !completor.equals(myself)).orElse(false))
                .anyMatch(freshTask -> previousVersion.getTasks().stream()
                        .filter(existingTask -> existingTask.getDescription().equals(freshTask.getDescription()))
                        .findAny()
                        .filter(previousTaskVersion -> !previousTaskVersion.isCompleted())
                        .isPresent());

        return !mr.isWip() && anyTaskNewlyCompletedNotByMyself;
    }

    @Override
    public String getTitle() {
        final GitLabMergeRequest mr = this.getMergeRequest();
        final long completedTasksCount = mr.completedTasksSince(this.getPreviousVersion());
        final String s = completedTasksCount > 1 ? "s" : "";
        return String.format("%d completed task%s on merge request\n%s", completedTasksCount, s, mr.getIdentifier());
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
