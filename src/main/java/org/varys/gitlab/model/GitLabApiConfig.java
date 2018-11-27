package org.varys.gitlab.model;

public class GitLabApiConfig {

    private final String baseUrl;
    private final String privateToken;

    public GitLabApiConfig(String baseUrl, String privateToken) {
        this.baseUrl = baseUrl;
        this.privateToken = privateToken;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getPrivateToken() {
        return privateToken;
    }

    @Override
    public String toString() {
        return "GitLabApiConfig{" +
                "baseUrl='" + baseUrl + '\'' +
                ", privateToken='" + "<hidden>" + '\'' +
                '}';
    }
}
