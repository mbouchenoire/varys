package org.varys.gitlab.model.notification;

import org.varys.gitlab.model.GitLabMergeRequest;

abstract class MergeRequestUpdateNotification extends MergeRequestNotification {

    private final GitLabMergeRequest previousVersion;

    MergeRequestUpdateNotification(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion) {
        super(mergeRequest);
        this.previousVersion = previousVersion;
    }

    abstract public boolean shouldNotify();

    GitLabMergeRequest getPreviousVersion() {
        return previousVersion;
    }
}
