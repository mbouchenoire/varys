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
    public String getTitle() {
        final String previousAssignee = this.getPreviousVersion().getAssignee().getName().split(" ")[0];
        final String newAssignee = this.getMergeRequest().getAssignee().getName().split(" ")[0];

        return "Merge request changed assignee\n"
                + previousAssignee + " -> " + newAssignee;
    }

    @Override
    public NotificationType getType() {
        return NotificationType.WARNING;
    }
}
