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
    public String getHeader() {
        final long addedUserNotesCount = this.getMergeRequest().addedUserNotesCount(this.getPreviousVersion());
        final String s = addedUserNotesCount > 1 ? "s" : "";
        return String.format("%d new comment%s on merge request", addedUserNotesCount, s);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
