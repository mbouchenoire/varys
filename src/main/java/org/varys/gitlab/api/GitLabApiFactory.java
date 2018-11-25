package org.varys.gitlab.api;

import org.varys.common.service.Log;
import org.varys.gitlab.model.GitLabApiConfig;

public final class GitLabApiFactory {

    private GitLabApiFactory() {
        super();
    }

    public static GitLabApi create(GitLabApiConfig apiConfig) {
        final GitLabApi apiv4 = new GitLabApiV4(apiConfig);

        if (apiv4.isOnline()) {
            Log.info("GitLab API v4 has been detected");
            return apiv4;
        } else {
            Log.info("GitLab API v4 is not responding, using API v3");
            return new GitLabApiV3(apiConfig);
        }
    }
}
