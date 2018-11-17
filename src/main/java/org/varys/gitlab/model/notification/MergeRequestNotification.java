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

    @Override
    public String getDescription() {
        return this.description;
    }

    private static String formatMergeRequestDescription(GitLabMergeRequest mergeRequest) {
        final String descriptionTemplate =
                "%s\n" +
                        "%s\n" +
                        "%s into %s\n" +
                        "by %s for %s";

        return String.format(descriptionTemplate,
                mergeRequest.getTitle(),
                mergeRequest.getIdentifier(),
                mergeRequest.getSourceBranch(), mergeRequest.getTargetBranch(),
                mergeRequest.getAuthor().getName(), mergeRequest.getAssignee().getName()
        );
    }
}
