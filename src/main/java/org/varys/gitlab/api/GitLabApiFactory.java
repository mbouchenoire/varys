package org.varys.gitlab.api;

import org.varys.gitlab.model.GitLabApiConfig;

public final class GitLabApiFactory {

    private GitLabApiFactory() {
        super();
    }

    public static GitLabApi create(GitLabApiConfig apiConfig) {
        switch (apiConfig.getApiVersion()) {
            case 3:
                return new GitLabApiV3(apiConfig);
            case 4:
                return new GitLabApiV4(apiConfig);
            default:
                throw new IllegalArgumentException("Unknown GitLab API version: " + apiConfig);
        }
    }
}
