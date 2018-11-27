package org.varys.gitlab.api;

import org.varys.common.service.Log;
import org.varys.gitlab.model.GitLabApiConfig;

public class GitLabApiFactory {

    public GitLabApi create(GitLabApiConfig apiConfig) {
        final GitLabApiV3 apiv3 = new GitLabApiV3(apiConfig);
        final GitLabApiV4 apiv4 = new GitLabApiV4(apiConfig);

        if (!apiv3.isAuthorized() && !apiv4.isAuthorized()) {
            throw new IllegalArgumentException("Failed to authenticate agains't GitLab API (verify your private token)");
        }

        if (apiv3.isCompatible()) {
            Log.info("Using compatible GitLab API v3");
            return apiv3;
        } else if (apiv4.isCompatible()) {
            Log.info("GitLab API v3 is not compatible, using compatible API v4");
            return apiv4;
        } else {
            throw new UnsupportedOperationException("Cannot find compatible GitLab API version");
        }
    }
}
