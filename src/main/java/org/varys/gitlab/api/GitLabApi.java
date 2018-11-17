package org.varys.gitlab.api;

import org.varys.common.RestApi;
import org.varys.gitlab.model.GitLabMergeRequest;
import org.varys.gitlab.model.GitLabMergeRequestState;

import java.util.List;
import java.util.Optional;

public interface GitLabApi extends RestApi {

    List<GitLabMergeRequest> getMergeRequests(GitLabMergeRequestState state);
    Optional<GitLabMergeRequest> getMergeRequest(long projectId, long mergeRequestId, long mergeRequestIid);
}
