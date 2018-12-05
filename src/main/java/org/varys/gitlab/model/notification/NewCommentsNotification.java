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
