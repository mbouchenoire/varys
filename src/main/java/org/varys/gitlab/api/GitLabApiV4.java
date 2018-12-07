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

import org.apache.http.HttpStatus;
import org.pmw.tinylog.Logger;
import org.varys.common.service.OkHttpClientFactory;
import org.varys.gitlab.model.GitLabApiConfig;
import org.varys.gitlab.model.GitLabCommit;
import org.varys.gitlab.model.GitLabMergeRequest;
import org.varys.gitlab.model.GitLabMergeRequestListItem;
import org.varys.gitlab.model.GitLabMergeRequestState;
import org.varys.gitlab.model.GitLabNote;
import org.varys.gitlab.model.GitLabProject;
import org.varys.gitlab.model.GitLabUser;
import org.varys.gitlab.model.GitLabVersion;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GitLabApiV4 implements GitLabApi {

    private final GitLabApiConfig apiConfig;
    private final GitLabApiV4Retrofit gitLabApiV4Retrofit;

    public GitLabApiV4(GitLabApiConfig apiConfig) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiConfig.getBaseUrl())
                .client(OkHttpClientFactory.create(apiConfig.isSslVerify()))
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        this.gitLabApiV4Retrofit = retrofit.create(GitLabApiV4Retrofit.class);
        this.apiConfig = apiConfig;
    }

    @Override
    public int getVersion() {
        return 4;
    }

    @Override
    public boolean isAuthorized() {
        try {
            final Response<GitLabVersion> response =
                    this.gitLabApiV4Retrofit.getVersion(this.apiConfig.getPrivateToken()).execute();

            return response.code() != HttpStatus.SC_NOT_FOUND && response.code() != HttpStatus.SC_UNAUTHORIZED;
        } catch (IOException e) {
            Logger.error(e, "Authentication query agains't GitLab v3 API failed");
            return false;
        }
    }

    @Override
    public boolean isCompatible() {
        try {
            final Response<GitLabVersion> response =
                    this.gitLabApiV4Retrofit.getVersion(this.apiConfig.getPrivateToken()).execute();

            if (!response.isSuccessful()) {
                return false;
            }

            final GitLabVersion version = response.body();

            assert version != null;

            if (version.getMajor() == 9) {
                return version.getMinor() >= 5;
            } else {
                return version.getMajor() >= 10;
            }
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isOnline() {
        try {
            final Response<GitLabUser> response =
                    this.gitLabApiV4Retrofit.getUser(this.apiConfig.getPrivateToken()).execute();

            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String getBaseUrl() {
        return this.apiConfig.getBaseUrl();
    }

    @Override
    public GitLabUser getUser() {
        return this.getUser(() -> this.gitLabApiV4Retrofit.getUser(this.apiConfig.getPrivateToken()));
    }

    @Override
    public List<GitLabMergeRequest> getMergeRequests(GitLabMergeRequestState state) {
        try {
            final Response<List<GitLabMergeRequestListItem>> response = this.gitLabApiV4Retrofit.getMergeRequests(
                    this.apiConfig.getPrivateToken(), state.getCode()).execute();

            if (response.isSuccessful()) {
                final List<GitLabMergeRequestListItem> listItems = response.body();
                assert listItems != null;
                Logger.debug("Fetched {} GitLab merge request(s) (opened)", listItems.size());
                return listItems.parallelStream()
                        .map(this::fetchDetails)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());
            } else {
                throw new IOException(response.message());
            }
        } catch (IOException e) {
            Logger.error(e, "Failed to retreive GitLab merge requests");
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<GitLabMergeRequest> getMergeRequest(long projectId, long mergeRequestId, long mergeRequestIid) {
        try {
            final GitLabMergeRequestListItem mergeRequestListItem = this.gitLabApiV4Retrofit.getMergeRequest(
                    this.apiConfig.getPrivateToken(), projectId, mergeRequestIid).execute().body();

            if (mergeRequestListItem != null) {
                return this.fetchDetails(mergeRequestListItem);
            } else {
                Logger.error("Failed to fetch GitLab merge request with iid={} within project with id={}",
                        mergeRequestIid, projectId);

                return Optional.empty();
            }
        } catch (IOException e) {
            Logger.error(e, "Failed to fetch GitLab merge request with project_id={} and iid={}",
                    projectId,
                    mergeRequestIid);

            return Optional.empty();
        }
    }

    private Optional<GitLabMergeRequest> fetchDetails(GitLabMergeRequestListItem mergeRequest) {
        final long projectId = mergeRequest.getProjectId();
        final long mergeRequestIid = mergeRequest.getIid();

        return this.getProject(projectId)
                .map(project -> {
                    final List<GitLabNote> notes = this.getNotes(projectId, mergeRequestIid);
                    final List<GitLabCommit> commits = this.getCommits(projectId, mergeRequestIid);
                    return new GitLabMergeRequest(mergeRequest, project, notes, commits);
                });
    }

    private Optional<GitLabProject> getProject(long projectId) {
        try {
            final GitLabProject gitLabProject = this.gitLabApiV4Retrofit.getProject(
                    this.apiConfig.getPrivateToken(), projectId).execute().body();

            if (gitLabProject != null) {
                return Optional.of(gitLabProject);
            } else {
                Logger.error("Failed to fetch GitLab project with id={}", projectId);
                return Optional.empty();
            }
        } catch (IOException e) {
            Logger.error(e, "Failed to fetch GitLab project with id={}", projectId);
            return Optional.empty();
        }
    }

    private List<GitLabNote> getNotes(long projectId, long mergeRequestIid) {
        try {
            return this.gitLabApiV4Retrofit.getNotes(
                    this.apiConfig.getPrivateToken(), projectId, mergeRequestIid).execute().body();
        } catch (IOException e) {
            Logger.error(e, "Failed to fetch GitLab notes for merge request with project_id={} and iid={}",
                    projectId,
                    mergeRequestIid);

            return Collections.emptyList();
        }
    }

    private List<GitLabCommit> getCommits(long projectId, long mergeRequestIid) {
        try {
            return this.gitLabApiV4Retrofit.getCommits(
                    this.apiConfig.getPrivateToken(), projectId, mergeRequestIid).execute().body();
        } catch (IOException e) {
            Logger.error(e, "Failed to fetch GitLab commits for merge request with project_id={} and iid={}",
                    projectId,
                    mergeRequestIid);

            return Collections.emptyList();
        }
    }

    @Override
    public String toString() {
        return "GitLabApiV4{" +
                "apiConfig=" + apiConfig +
                '}';
    }
}
