package org.varys.gitlab.model.notification;

import org.varys.common.model.NotificationType;
import org.varys.gitlab.model.GitLabMergeRequest;

class MergedNotification extends MergeRequestUpdateNotification {

    MergedNotification(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion) {
        super(mergeRequest, previousVersion);
    }

    @Override
    public boolean shouldNotify() {
        return this.getMergeRequest().isMerged() && !this.getPreviousVersion().isMerged();
    }

    @Override
    public String getTitle() {
        return "Merge request has been merged\n" + this.getMergeRequest().getTitle();
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
