package org.varys.gitlab.api;

import org.varys.common.service.Log;

public class GitLabApiLocator {

    public GitLabApi findUsable(GitLabApiV3 gitLabApiV3, GitLabApiV4 gitLabApiV4) {
        if (!gitLabApiV3.isAuthorized() && !gitLabApiV4.isAuthorized()) {
            throw new IllegalArgumentException("Failed to authenticate agains't GitLab API (verify your private token)");
        }

        if (gitLabApiV3.isCompatible()) {
            Log.info("Using compatible GitLab API v3");
            return gitLabApiV3;
        } else if (gitLabApiV4.isCompatible()) {
            Log.info("GitLab API v3 is not compatible, using compatible API v4");
            return gitLabApiV4;
        } else {
            throw new UnsupportedOperationException("Cannot find compatible GitLab API version");
        }
    }
}
