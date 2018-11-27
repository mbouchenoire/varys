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

        return mr.getAssignee().equals(myself) && !mr.isWip() && mr.addedCommitsCount(previousVersion) > 0;
    }

    @Override
    public String getHeader() {
        final long addedCommitsCount = this.getMergeRequest().addedCommitsCount(this.getPreviousVersion());
        final String s = addedCommitsCount > 1 ? "s" : "";
        return String.format("%d new commit%s on merge request", addedCommitsCount, s);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
