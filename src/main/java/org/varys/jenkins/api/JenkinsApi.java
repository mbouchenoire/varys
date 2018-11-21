package org.varys.jenkins.api;

import org.varys.common.RestApi;
import org.varys.common.service.Log;
import org.varys.common.service.OkHttpClientFactory;
import org.varys.jenkins.model.JenkinsApiConfig;
import org.varys.jenkins.model.JenkinsBuild;
import org.varys.jenkins.model.JenkinsNode;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Optional;

public class JenkinsApi implements RestApi {

    private final String baseUrl;
    private final JenkinsRetrofitApi jenkinsRetrofitApi;
    private final JenkinsApiConfig apiConfig;

    public JenkinsApi(JenkinsApiConfig apiConfig) {
        this.baseUrl = apiConfig.getBaseUrl();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiConfig.getBaseUrl())
                .client(OkHttpClientFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        this.jenkinsRetrofitApi = retrofit.create(JenkinsRetrofitApi.class);

        this.apiConfig = apiConfig;
    }

    @Override
    public String getLabel() {
        return "Jenkins";
    }

    @Override
    public String getBaseUrl() {
        return this.baseUrl;
    }

    @Override
    public boolean isOnline() {
        try {
            final Response<JenkinsNode> response =
                    this.jenkinsRetrofitApi.getRootNode(this.apiConfig.getApiToken()).execute();

            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    public Optional<JenkinsNode> getRootNode() {
        try {
            return Optional.ofNullable(
                    this.jenkinsRetrofitApi.getRootNode(this.apiConfig.getApiToken()).execute().body());
        } catch (IOException e) {
            Log.error(e, "Failed to fetch Jenkins root node");
            return Optional.empty();
        }
    }

    public Optional<JenkinsNode> getNode(String url) {
        try {
            return Optional.ofNullable(
                    this.jenkinsRetrofitApi.getNode(url, this.apiConfig.getApiToken()).execute().body());
        } catch (IOException e) {
            Log.error(e, "Failed to fetch Jenkins node with url={}", url);
            return Optional.empty();
        }
    }

    public Optional<JenkinsBuild> getBuild(String url) {
        try {
            return Optional.ofNullable(
                    this.jenkinsRetrofitApi.getBuild(url, this.apiConfig.getApiToken()).execute().body());
        } catch (IOException e) {
            Log.error(e,"Failed to fetch Jenkins build with url={}", url);
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return "JenkinsApi{" +
                "apiConfig=" + apiConfig +
                '}';
    }
}
