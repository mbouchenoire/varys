/*
 * This file is part of Varys.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Varys.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.varys.gitlab.api;

import org.varys.gitlab.model.GitLabCommit;
import org.varys.gitlab.model.GitLabMergeRequestListItem;
import org.varys.gitlab.model.GitLabNote;
import org.varys.gitlab.model.GitLabProject;
import org.varys.gitlab.model.GitLabUser;
import org.varys.gitlab.model.GitLabVersion;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

interface GitLabApiV4Retrofit {

    @GET("api/v4/version")
    Call<GitLabVersion> getVersion(
            @Header("Private-Token") String privateToken);

    @GET("api/v4/user")
    Call<GitLabUser> getUser(
            @Header("Private-Token") String privateToken);

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
