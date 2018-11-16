package org.varys.gitlab.api;

import org.varys.common.RestApi;
import org.varys.gitlab.model.GitLabMergeRequestDetails;
import org.varys.gitlab.model.GitLabMergeRequestState;

import java.util.List;
import java.util.Optional;

public interface GitLabApi extends RestApi {

    List<GitLabMergeRequestDetails> getMergeRequests(GitLabMergeRequestState state);
    Optional<GitLabMergeRequestDetails> getMergeRequest(long projectId, long mergeRequestId, long mergeRequestIid);
}
