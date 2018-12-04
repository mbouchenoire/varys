package org.varys.gitlab.api;

import org.apache.http.HttpStatus;
import org.varys.common.service.Log;
import org.varys.common.service.OkHttpClientFactory;
import org.varys.gitlab.model.GitLabApiConfig;
import org.varys.gitlab.model.GitLabCommit;
import org.varys.gitlab.model.GitLabMergeRequest;
import org.varys.gitlab.model.GitLabMergeRequestListItem;
import org.varys.gitlab.model.GitLabMergeRequestState;
import org.varys.gitlab.model.GitLabNote;
import org.varys.gitlab.model.GitLabProject;
import org.varys.gitlab.model.GitLabProjectListItem;
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

public class GitLabApiV3 implements GitLabApi {

    private final GitLabApiConfig apiConfig;
    private final GitLabApiV3Retrofit gitLabApiV3Retrofit;

    public GitLabApiV3(GitLabApiConfig apiConfig) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiConfig.getBaseUrl())
                .client(OkHttpClientFactory.create(apiConfig.isSslVerify()))
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        this.gitLabApiV3Retrofit = retrofit.create(GitLabApiV3Retrofit.class);
        this.apiConfig = apiConfig;
    }

    @Override
    public int getVersion() {
        return 3;
    }

    @Override
    public boolean isAuthorized() {
        try {
            final Response<GitLabVersion> response =
                    this.gitLabApiV3Retrofit.getVersion(this.apiConfig.getPrivateToken()).execute();

            return response.code() != HttpStatus.SC_GONE && response.code() != HttpStatus.SC_UNAUTHORIZED;
        } catch (IOException e) {
            Log.error(e, "Authentication query agains't GitLab v3 API failed");
            return false;
        }
    }

    @Override
    public boolean isCompatible() {
        try {
            final Response<GitLabVersion> response =
                    this.gitLabApiV3Retrofit.getVersion(this.apiConfig.getPrivateToken()).execute();

            return response.code() != HttpStatus.SC_GONE;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isOnline() {
        try {
            final Response<GitLabUser> response =
                    this.gitLabApiV3Retrofit.getUser(this.apiConfig.getPrivateToken()).execute();

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
        return this.getUser(() -> this.gitLabApiV3Retrofit.getUser(this.apiConfig.getPrivateToken()));
    }

    @Override
    public List<GitLabMergeRequest> getMergeRequests(GitLabMergeRequestState state) {
        try {
            final Response<List<GitLabProjectListItem>> response = this.gitLabApiV3Retrofit.getProjects(this.apiConfig.getPrivateToken()).execute();

            if (response.isSuccessful()) {
                final List<GitLabProjectListItem> projectListItems = response.body();

                assert projectListItems != null;

                return projectListItems.parallelStream()
                        .flatMap(project -> this.getMergeRequests(project.getId(), state).stream())
                        .map(this::fetchDetails)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());
            } else {
                throw new IOException(response.message());
            }
        } catch (IOException e) {
            Log.error(e, "Failed to fetch GitLab merge requests with state=" + state);
            return Collections.emptyList();
        }
    }

    private List<GitLabMergeRequestListItem> getMergeRequests(String projectId, GitLabMergeRequestState state) {
        try {
            return this.gitLabApiV3Retrofit.getMergeRequests(
                    this.apiConfig.getPrivateToken(),
                    Long.parseLong(projectId),
                    state.getCode(),
                    null
            ).execute().body();
        } catch (IOException e) {
            Log.error(e, "Failed to fetch GitLab merge requests of project with id=" + projectId);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<GitLabMergeRequest> getMergeRequest(long projectId, long mergeRequestId, long mergeRequestIid) {
        try {
            final Response<List<GitLabMergeRequestListItem>> response = this.gitLabApiV3Retrofit.getMergeRequests(
                    this.apiConfig.getPrivateToken(),
                    projectId,
                    null,
                    String.valueOf(mergeRequestIid)
            ).execute();

            if (response.isSuccessful()) {
                assert response.body() != null;

                final GitLabMergeRequestListItem mergeRequestListItem = response.body().stream()
                        .findFirst()
                        .orElseThrow(IOException::new);

                return this.fetchDetails(mergeRequestListItem);
            } else {
                throw new IOException(response.message());
            }
        } catch (IOException e) {
            Log.error(e, "Failed to fetch GitLab merge request with project_id={} and id={}",
                    projectId,
                    mergeRequestId);

            return Optional.empty();
        }
    }

    private Optional<GitLabMergeRequest> fetchDetails(GitLabMergeRequestListItem mergeRequest) {
        final long projectId = mergeRequest.getProjectId();
        final long mergeRequestId = mergeRequest.getId();
        final long mergeRequestIid = mergeRequest.getIid();

        return this.getProject(projectId)
                .map(project -> {
                    final List<GitLabNote> notes = this.getNotes(projectId, mergeRequestId, mergeRequestIid);
                    final List<GitLabCommit> commits = this.getCommits(projectId, mergeRequestId, mergeRequestIid);
                    return new GitLabMergeRequest(mergeRequest, project, notes, commits);
                });
    }

    private Optional<GitLabProject> getProject(long projectId) {
        try {
            final Response<GitLabProject> response = this.gitLabApiV3Retrofit.getProject(
                    this.apiConfig.getPrivateToken(), projectId).execute();

            if (response.isSuccessful()) {
                final GitLabProject gitLabProject = response.body();
                assert gitLabProject != null;
                return Optional.of(gitLabProject);
            } else {
                throw new IOException(response.message());
            }
        } catch (IOException e) {
            Log.error(e, "Failed to fetch GitLab project with id={}", projectId);
            return Optional.empty();
        }
    }

    private List<GitLabNote> getNotes(long projectId, long mergeRequestId, long mergeRequestIid) {
        try {
            return this.gitLabApiV3Retrofit.getNotes(
                    this.apiConfig.getPrivateToken(), projectId, mergeRequestId).execute().body();
        } catch (IOException e) {
            Log.error(e, "Failed to fetch GitLab notes for merge request with project_id={}, id={}, iid={}",
                    projectId,
                    mergeRequestId,
                    mergeRequestIid);

            return Collections.emptyList();
        }
    }

    private List<GitLabCommit> getCommits(long projectId, long mergeRequestId, long mergeRequestIid) {
        try {
            return this.gitLabApiV3Retrofit.getCommits(
                    this.apiConfig.getPrivateToken(), projectId, mergeRequestId).execute().body();
        } catch (IOException e) {
            Log.error(e, "Failed to fetch GitLab commits for merge request with project_id={}, id={}, iid={}",
                    projectId,
                    mergeRequestId,
                    mergeRequestIid);

            return Collections.emptyList();
        }
    }

    @Override
    public String toString() {
        return "GitLabApiV3{" +
                "apiConfig=" + apiConfig +
                '}';
    }
}
