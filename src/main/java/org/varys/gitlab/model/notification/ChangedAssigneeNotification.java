package org.varys.gitlab.model.notification;

import org.varys.common.model.NotificationType;
import org.varys.gitlab.model.GitLabMergeRequest;

class ChangedAssigneeNotification extends MergeRequestUpdateNotification {

    ChangedAssigneeNotification(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion) {
        super(mergeRequest, previousVersion);
    }

    @Override
    public boolean shouldNotify() {
        return !this.getMergeRequest().sameAssignee(this.getPreviousVersion());
    }

    @Override
    public String getHeader() {
        return "Merge request changed assignee";
    }

    @Override
    public NotificationType getType() {
        return NotificationType.WARNING;
    }
}
