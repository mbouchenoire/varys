package org.varys.gitlab.api;

import org.varys.common.RestApi;
import org.varys.gitlab.model.GitLabMergeRequest;
import org.varys.gitlab.model.GitLabMergeRequestState;
import org.varys.gitlab.model.GitLabUser;

import java.util.List;
import java.util.Optional;

public interface GitLabApi extends RestApi {

    @Override
    default String getLabel() {
        return "GitLab";
    }

    boolean isAuthorized();
    boolean isCompatible();
    GitLabUser getUser();
    List<GitLabMergeRequest> getMergeRequests(GitLabMergeRequestState state);
    Optional<GitLabMergeRequest> getMergeRequest(long projectId, long mergeRequestId, long mergeRequestIid);
}
