package org.varys.gitlab.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GitLabMergeRequestState {
    @JsonProperty("opened")
    OPENED("opened"),
    @JsonProperty("closed")
    CLOSED("closed"),
    @JsonProperty("locked")
    LOCKED("locked"),
    @JsonProperty("merged")
    MERGED("merged");

    private final String code;

    GitLabMergeRequestState(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
