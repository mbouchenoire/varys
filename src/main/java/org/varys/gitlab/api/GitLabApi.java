package org.varys.gitlab.api;

import org.varys.common.RestApi;
import org.varys.common.service.Log;
import org.varys.gitlab.model.GitLabApiConfig;
import org.varys.gitlab.model.GitLabMergeRequest;
import org.varys.gitlab.model.GitLabMergeRequestState;
import org.varys.gitlab.model.GitLabUser;
import retrofit2.Response;

import java.io.IOException;
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

    default GitLabUser getUser(GitLabApiRetrofit gitLabApiRetrofit, GitLabApiConfig apiConfig) {
        try {
            final Response<GitLabUser> response =
                    gitLabApiRetrofit.getUser(apiConfig.getPrivateToken()).execute();

            if (response.isSuccessful()) {
                return response.body();
            } else {
                throw new IOException(response.message());
            }
        } catch (IOException e) {
            final String msg = "Failed to fetch GitLab user";
            Log.error(e, msg);
            throw new IllegalStateException(msg);
        }
    }
}
