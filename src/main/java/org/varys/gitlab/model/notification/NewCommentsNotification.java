package org.varys.gitlab.model.notification;

import org.varys.common.model.NotificationType;
import org.varys.gitlab.model.GitLabMergeRequest;

class NewCommentsNotification extends MergeRequestUpdateNotification {

    NewCommentsNotification(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion) {
        super(mergeRequest, previousVersion);
    }

    @Override
    public boolean shouldNotify() {
        return this.getMergeRequest().addedUserNotesCount(this.getPreviousVersion()) > 0;
    }

    @Override
    public String getTitle() {
        final long addedUserNotesCount = this.getMergeRequest().addedUserNotesCount(this.getPreviousVersion());
        final String s = addedUserNotesCount > 1 ? "s" : "";
        return String.format("%d new comments%s on merge request\n%s",
                addedUserNotesCount, s, this.getMergeRequest().getTitle());
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
