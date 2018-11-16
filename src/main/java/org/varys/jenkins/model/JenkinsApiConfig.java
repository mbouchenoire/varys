package org.varys.jenkins.model;

public class JenkinsApiConfig {

    private final String baseUrl;
    private final String apiToken;

    public JenkinsApiConfig(String baseUrl, String apiToken) {
        this.baseUrl = baseUrl;
        this.apiToken = apiToken;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiToken() {
        return apiToken;
    }

    @Override
    public String toString() {
        return "JenkinsApiConfig{" +
                "baseUrl='" + baseUrl + '\'' +
                ", apiToken='" + "<hidden>" + '\'' +
                '}';
    }
}
