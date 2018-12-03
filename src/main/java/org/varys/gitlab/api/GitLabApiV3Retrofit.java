package org.varys.gitlab.api;

import org.varys.gitlab.model.GitLabCommit;
import org.varys.gitlab.model.GitLabMergeRequestListItem;
import org.varys.gitlab.model.GitLabNote;
import org.varys.gitlab.model.GitLabProject;
import org.varys.gitlab.model.GitLabProjectListItem;
import org.varys.gitlab.model.GitLabUser;
import org.varys.gitlab.model.GitLabVersion;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

interface GitLabApiV3Retrofit extends GitLabApiRetrofit {

    @GET("api/v3/version")
    Call<GitLabVersion> getVersion(
            @Header("Private-Token") String privateToken);

    @GET("api/v3/user")
    Call<GitLabUser> getUser(
            @Header("Private-Token") String privateToken);

    @GET("api/v3/projects")
    Call<List<GitLabProjectListItem>> getProjects(
            @Header("Private-Token") String privateToken);

    @GET("api/v3/projects/{project_id}")
    Call<GitLabProject> getProject(
            @Header("Private-Token") String privateToken,
            @Path("project_id") long projectId);

    @GET("api/v3/projects/{project_id}/merge_requests")
    Call<List<GitLabMergeRequestListItem>> getMergeRequests(
            @Header("Private-Token") String privateToken,
            @Path("project_id") long projectId,
            @Query("state") String state,
            @Query("iid") String iid);

    @GET("api/v3/projects/{project_id}/merge_requests/{merge_request_id}/notes")
    Call<List<GitLabNote>> getNotes(
            @Header("Private-Token") String privateToken,
            @Path("project_id") long projectId,
            @Path("merge_request_id") long mergeRequestId);

    @GET("api/v3/projects/{project_id}/merge_requests/{merge_request_id}/commits")
    Call<List<GitLabCommit>> getCommits(
            @Header("Private-Token") String privateToken,
            @Path("project_id") long projectId,
            @Path("merge_request_id") long mergeRequestId);
}
