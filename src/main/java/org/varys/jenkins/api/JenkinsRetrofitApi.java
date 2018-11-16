package org.varys.jenkins.api;

import org.varys.jenkins.model.JenkinsBuild;
import org.varys.jenkins.model.JenkinsNode;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

interface JenkinsRetrofitApi {

    @GET("api/json")
    Call<JenkinsNode> getRootNode(@Query("token") String apiToken);

    @GET
    Call<JenkinsNode> getNode(@Url String url, @Query("token") String apiToken);

    @GET
    Call<JenkinsBuild> getBuild(@Url String url, @Query("token") String apiToken);
}
