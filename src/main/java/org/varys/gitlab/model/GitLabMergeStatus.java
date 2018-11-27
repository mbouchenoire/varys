package org.varys.gitlab.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GitLabMergeStatus {
    @JsonProperty("can_be_merged")
    CAN_BE_MERGED,
    @JsonProperty("cannot_be_merged")
    CANNOT_BE_MERGED,
    @JsonProperty("unchecked")
    UNCHECKED
}
