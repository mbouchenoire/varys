package org.varys.jenkins.model;

public class JenkinsApiConfig {

    private final String baseUrl;
    private final String apiToken;
    private final boolean sslVerify;

    public JenkinsApiConfig(String baseUrl, String apiToken, boolean sslVerify) {
        this.baseUrl = baseUrl;
        this.apiToken = apiToken;
        this.sslVerify = sslVerify;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiToken() {
        return apiToken;
    }

    public boolean isSslVerify() {
        return sslVerify;
    }

    @Override
    public String toString() {
        return "JenkinsApiConfig{" +
                "baseUrl='" + baseUrl + '\'' +
                ", apiToken='" + "<hidden>" + '\'' +
                ", sslVerify='" + sslVerify + '\'' +
                '}';
    }
}
