package org.varys.gitlab.model.notification;

import org.varys.common.model.NotificationType;
import org.varys.gitlab.model.GitLabMergeRequest;

public class NewMergeRequestNotification extends MergeRequestNotification {

    private final GitLabMergeRequest mergeRequest;

    public NewMergeRequestNotification(GitLabMergeRequest mergeRequest) {
        super(mergeRequest);
        this.mergeRequest = mergeRequest;
    }

    @Override
    public String getTitle() {
        return "New merge request\n" + this.mergeRequest.getTitle();
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
