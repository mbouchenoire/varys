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
        final String authorName = mergeRequest.getAuthor().getName().split(" ")[0];
        final String assigneeName = mergeRequest.getAssignee().getName().split(" ")[0];

        return "New merge request on " + this.mergeRequest.getProject().getName() + "\n"
                + "By " + authorName + " for " + assigneeName;
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
