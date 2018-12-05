package org.varys.gitlab.model.notification;

import org.varys.common.model.NotificationType;
import org.varys.gitlab.model.GitLabMergeRequest;

class ClosedNotification extends MergeRequestUpdateNotification {

    ClosedNotification(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion) {
        super(mergeRequest, previousVersion);
    }

    @Override
    public boolean shouldNotify() {
        return this.getMergeRequest().isClosed() && !this.getPreviousVersion().isClosed();
    }

    @Override
    public String getTitle() {
        return "Merge request has been closed\n"
                + this.getMergeRequest().getIdentifier();
    }

    @Override
    public NotificationType getType() {
        return NotificationType.WARNING;
    }
}
