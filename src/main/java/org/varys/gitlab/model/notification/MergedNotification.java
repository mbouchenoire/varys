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
    public String getHeader() {
        return "Merge request has been merged";
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
