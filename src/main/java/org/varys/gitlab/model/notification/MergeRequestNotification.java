package org.varys.gitlab.model.notification;

import org.varys.common.model.Notification;
import org.varys.gitlab.model.GitLabMergeRequest;

public abstract class MergeRequestNotification implements Notification {

    private final GitLabMergeRequest mergeRequest;
    private final String description;

    MergeRequestNotification(GitLabMergeRequest mergeRequest) {
        this.mergeRequest = mergeRequest;
        this.description = formatMergeRequestDescription(mergeRequest);
    }

    protected GitLabMergeRequest getMergeRequest() {
        return mergeRequest;
    }

    protected abstract String getHeader();

    @Override
    public String getTitle() {
        final String authorName = mergeRequest.getAuthor().getName().split(" ")[0];
        final String assigneeName = mergeRequest.getAssignee().getName().split(" ")[0];
        return this.getHeader() + "\nBy " + authorName + " for " + assigneeName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    private static String formatMergeRequestDescription(GitLabMergeRequest mergeRequest) {
        return String.format("%s\n%s\n%s into %s",
                mergeRequest.getTitle(),
                mergeRequest.getIdentifier(),
                mergeRequest.getSourceBranch(), mergeRequest.getTargetBranch()
        );
    }
}
