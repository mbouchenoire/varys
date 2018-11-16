package org.varys.gitlab.model;

public class GitLabApiConfig {

    private final int apiVersion;
    private final String baseUrl;
    private final String privateToken;

    public GitLabApiConfig(int apiVersion, String baseUrl, String privateToken) {
        this.apiVersion = apiVersion;
        this.baseUrl = baseUrl;
        this.privateToken = privateToken;
    }

    public int getApiVersion() {
        return apiVersion;
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
                "apiVersion='" + apiVersion + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", privateToken='" + "<hidden>" + '\'' +
                '}';
    }
}
