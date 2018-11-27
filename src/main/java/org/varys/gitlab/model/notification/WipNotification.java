package org.varys.gitlab.model.notification;

import org.varys.common.model.NotificationType;
import org.varys.gitlab.model.GitLabMergeRequest;
import org.varys.gitlab.model.GitLabUser;

public class WipNotification extends MergeRequestUpdateNotification {

    private final GitLabUser myself;

    WipNotification(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion, GitLabUser myself) {
        super(mergeRequest, previousVersion);
        this.myself = myself;
    }

    @Override
    public boolean shouldNotify() {
        final GitLabMergeRequest mr = this.getMergeRequest();
        final GitLabMergeRequest previousVersion = getPreviousVersion();

        return mr.getAssignee().equals(myself) && mr.isWip() != previousVersion.isWip();
    }

    @Override
    public String getHeader() {
        return this.getMergeRequest().isWip()
                ? "Merge request is now WIP"
                : "Merge request is no longer WIP";
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
