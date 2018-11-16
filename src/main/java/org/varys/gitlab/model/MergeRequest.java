package org.varys.gitlab.model;

public interface MergeRequest {

    long getId();
    long getIid();

    default boolean isSameMergeRequest(MergeRequest other) {
        return this.getId() == other.getId();
    }
}
