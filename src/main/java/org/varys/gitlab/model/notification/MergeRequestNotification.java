package org.varys.gitlab.model.notification;

import org.varys.common.model.Linkable;
import org.varys.common.model.Notification;
import org.varys.gitlab.model.GitLabMergeRequest;

import java.util.Objects;
import java.util.Optional;

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
    public Optional<String> getDescription() {
        return Optional.ofNullable(this.description);
    }

    @Override
    public Optional<Linkable> getLinkable() {
        return Optional.of(this.mergeRequest);
    }

    private static String formatMergeRequestDescription(GitLabMergeRequest mergeRequest) {
        return String.format("%s%n%s into %s",
                mergeRequest.getTitle(),
                mergeRequest.getSourceBranch(), mergeRequest.getTargetBranch()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MergeRequestNotification that = (MergeRequestNotification) o;
        return Objects.equals(mergeRequest, that.mergeRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mergeRequest);
    }
}
