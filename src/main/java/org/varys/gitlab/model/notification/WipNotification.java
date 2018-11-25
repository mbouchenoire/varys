package org.varys.gitlab.model.notification;

import org.varys.common.model.NotificationType;
import org.varys.gitlab.model.GitLabMergeRequest;

public class WipNotification extends MergeRequestUpdateNotification {

    public WipNotification(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion) {
        super(mergeRequest, previousVersion);
    }

    @Override
    public boolean shouldNotify() {
        return this.getMergeRequest().isWip() != getPreviousVersion().isWip();
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
