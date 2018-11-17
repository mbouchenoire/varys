package org.varys.gitlab.model.notification;

import org.varys.common.model.NotificationType;
import org.varys.gitlab.model.GitLabMergeRequest;

class StatusChangedNotification extends MergeRequestUpdateNotification {

    StatusChangedNotification(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion) {
        super(mergeRequest, previousVersion);
    }

    @Override
    public boolean shouldNotify() {
        return !this.getMergeRequest().getState().equals(this.getPreviousVersion().getState());
    }

    @Override
    public String getTitle() {
        return "Merge request changed status\n" + this.getMergeRequest().getTitle();
    }

    @Override
    public NotificationType getType() {
        return NotificationType.WARNING;
    }
}
