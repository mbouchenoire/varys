package org.varys.gitlab.model.notification;

import org.varys.common.model.NotificationType;
import org.varys.gitlab.model.GitLabMergeRequest;
import org.varys.gitlab.model.GitLabUser;

public class ConflictedMergeRequestNotification extends MergeRequestUpdateNotification {

    private GitLabUser myself;

    ConflictedMergeRequestNotification(
            GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion, GitLabUser myself) {

        super(mergeRequest, previousVersion);
        this.myself = myself;
    }

    @Override
    public boolean shouldNotify() {
        final GitLabMergeRequest mr = this.getMergeRequest();
        final GitLabMergeRequest previousVersion = this.getPreviousVersion();

        if (mr.hasConflict() == previousVersion.hasConflict()) {
            return false;
        }

        if (mr.hasConflict()) {
            return mr.getAuthor().equals(myself);
        } else {
            return mr.getAssignee().equals(myself);
        }
    }

    @Override
    public String getHeader() {
        final GitLabMergeRequest mr = this.getMergeRequest();

        return mr.hasConflict()
                ? "Conflicted merge request on " + mr.getProject().getName()
                : "Merge request no longer conflicted";
    }

    @Override
    public NotificationType getType() {
        return this.getMergeRequest().hasConflict()
                ? NotificationType.WARNING
                : NotificationType.INFO;
    }
}
