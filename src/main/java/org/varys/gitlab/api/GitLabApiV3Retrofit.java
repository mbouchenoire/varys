package org.varys.gitlab.api;

import org.varys.gitlab.model.GitLabCommit;
import org.varys.gitlab.model.GitLabMergeRequestListItem;
import org.varys.gitlab.model.GitLabNote;
import org.varys.gitlab.model.GitLabProject;
import org.varys.gitlab.model.GitLabProjectListItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

interface GitLabApiV3Retrofit {

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
            @Query("state") String state);

    @GET("api/v3/projects/{project_id}/merge_requests/{merge_request_id}")
    Call<GitLabMergeRequestListItem> getMergeRequest(
            @Header("Private-Token") String privateToken,
            @Path("project_id") long projectId,
            @Path("merge_request_id") long mergeRequestId);

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