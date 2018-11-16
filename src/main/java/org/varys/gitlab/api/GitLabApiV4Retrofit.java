package org.varys.gitlab.api;

import org.varys.gitlab.model.GitLabCommit;
import org.varys.gitlab.model.GitLabMergeRequestListItem;
import org.varys.gitlab.model.GitLabNote;
import org.varys.gitlab.model.GitLabProject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

interface GitLabApiV4Retrofit {

    @GET("api/v4/merge_requests")
    Call<List<GitLabMergeRequestListItem>> getMergeRequests(
            @Header("Private-Token") String privateToken,
            @Query("state") String state);

    @GET("api/v4/projects/{project_id}/merge_requests/{merge_request_iid}")
    Call<GitLabMergeRequestListItem> getMergeRequest(
            @Header("Private-Token") String privateToken,
            @Path("project_id") long projectId,
            @Path("merge_request_iid") long mergeRequestIid);

    @GET("api/v4/projects/{project_id}")
    Call<GitLabProject> getProject(
            @Header("Private-Token") String privateToken,
            @Path("project_id") long projectId);

    @GET("api/v4/projects/{project_id}/merge_requests/{merge_request_iid}/notes")
    Call<List<GitLabNote>> getNotes(
            @Header("Private-Token") String privateToken,
            @Path("project_id") long projectId,
            @Path("merge_request_iid") long mergeRequestIid);

    @GET("api/v4/projects/{project_id}/merge_requests/{merge_request_iid}/commits")
    Call<List<GitLabCommit>> getCommits(
            @Header("Private-Token") String privateToken,
            @Path("project_id") long projectId,
            @Path("merge_request_iid") long mergeRequestIid);
}
