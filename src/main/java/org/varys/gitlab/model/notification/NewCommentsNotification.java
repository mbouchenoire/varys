package org.varys.gitlab.model.notification;

import org.varys.common.model.NotificationType;
import org.varys.gitlab.model.GitLabMergeRequest;

class NewCommentsNotification extends MergeRequestUpdateNotification {

    NewCommentsNotification(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion) {
        super(mergeRequest, previousVersion);
    }

    @Override
    public boolean shouldNotify() {
        return !this.getMergeRequest().isWip()
                && this.getMergeRequest().addedUserNotesCount(this.getPreviousVersion()) > 0;
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
