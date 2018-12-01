package org.varys.gitlab.model;

public class GitLabApiConfig {

    private final String baseUrl;
    private final String privateToken;
    private final boolean sslVerify;

    public GitLabApiConfig(String baseUrl, String privateToken, boolean sslVerify) {
        this.baseUrl = baseUrl;
        this.privateToken = privateToken;
        this.sslVerify = sslVerify;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getPrivateToken() {
        return privateToken;
    }

    public boolean isSslVerify() {
        return sslVerify;
    }

    @Override
    public String toString() {
        return "GitLabApiConfig{" +
                "baseUrl='" + baseUrl + '\'' +
                ", privateToken='" + "<hidden>" + '\'' +
                ", sslVerify='" + sslVerify + '\'' +
                '}';
    }
}
