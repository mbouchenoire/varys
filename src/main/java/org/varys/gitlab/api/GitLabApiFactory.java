package org.varys.gitlab.api;

import org.varys.common.service.Log;
import org.varys.gitlab.model.GitLabApiConfig;

public final class GitLabApiFactory {

    private GitLabApiFactory() {
        super();
    }

    public static GitLabApi create(GitLabApiConfig apiConfig) {
        final GitLabApi apiv3 = new GitLabApiV3(apiConfig);
        final GitLabApi apiv4 = new GitLabApiV4(apiConfig);

        if (apiv3.isCompatible()) {
            Log.info("Using compatible GitLab API v3");
            return apiv3;
        } else if (apiv4.isCompatible()) {
            Log.info("GitLab API v3 is not compatible, using compatible API v4");
            return apiv4;
        } else {
            Log.error("Cannot find compatible GitLab API version");
            throw new UnsupportedOperationException("Non-compatible GitLab API version");
        }
    }
}
