package org.varys.gitlab.api;

import org.varys.common.service.Log;
import org.varys.common.service.OkHttpClientFactory;
import org.varys.gitlab.model.GitLabApiConfig;
import org.varys.gitlab.model.GitLabCommit;
import org.varys.gitlab.model.GitLabMergeRequestDetails;
import org.varys.gitlab.model.GitLabMergeRequestListItem;
import org.varys.gitlab.model.GitLabMergeRequestState;
import org.varys.gitlab.model.GitLabNote;
import org.varys.gitlab.model.GitLabProject;
import org.varys.gitlab.model.GitLabProjectListItem;
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

    GitLabApiV3(GitLabApiConfig apiConfig) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiConfig.getBaseUrl())
                .client(OkHttpClientFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        this.gitLabApiV3Retrofit = retrofit.create(GitLabApiV3Retrofit.class);
        this.apiConfig = apiConfig;
    }

    @Override
    public String getBaseUrl() {
        return this.apiConfig.getBaseUrl();
    }

    @Override
    public List<GitLabMergeRequestDetails> getMergeRequests(GitLabMergeRequestState state) {
        try {
            final List<GitLabProjectListItem> projectListItems =
                    this.gitLabApiV3Retrofit.getProjects(this.apiConfig.getPrivateToken()).execute().body();

            if (projectListItems != null) {
                return projectListItems.parallelStream()
                        .flatMap(project -> this.getMergeRequests(project.getId(), state).stream())
                        .map(this::fetchDetails)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());
            } else {
                Log.error("Failed to fetch GitLab projects");
                return Collections.emptyList();
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
                    state.getCode()
            ).execute().body();
        } catch (IOException e) {
            Log.error(e, "Failed to fetch GitLab merge requests of project with id=" + projectId);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<GitLabMergeRequestDetails> getMergeRequest(long projectId, long mergeRequestId, long mergeRequestIid) {
        try {
            final GitLabMergeRequestListItem mergeRequestListItem = this.gitLabApiV3Retrofit.getMergeRequest(
                    this.apiConfig.getPrivateToken(), projectId, mergeRequestId).execute().body();

            if (mergeRequestListItem != null) {
                return this.fetchDetails(mergeRequestListItem);
            } else {
                Log.error("Failed to fetch GitLab merge request with id={} within project with id={}",
                        mergeRequestId, projectId);

                return Optional.empty();
            }
        } catch (IOException e) {
            Log.error(e, "Failed to fetch GitLab merge request with project_id={} and iid={}",
                    projectId,
                    mergeRequestIid);

            return Optional.empty();
        }
    }

    private Optional<GitLabMergeRequestDetails> fetchDetails(GitLabMergeRequestListItem mergeRequest) {
        final long projectId = mergeRequest.getProjectId();
        final long mergeRequestId = mergeRequest.getId();
        final long mergeRequestIid = mergeRequest.getIid();

        return this.getProject(projectId)
                .map(project -> {
                    final List<GitLabNote> notes = this.getNotes(projectId, mergeRequestId, mergeRequestIid);
                    final List<GitLabCommit> commits = this.getCommits(projectId, mergeRequestId, mergeRequestIid);
                    return new GitLabMergeRequestDetails(mergeRequest, project, notes, commits);
                });
    }

    private Optional<GitLabProject> getProject(long projectId) {
        try {
            final GitLabProject gitLabProject = this.gitLabApiV3Retrofit.getProject(
                    this.apiConfig.getPrivateToken(), projectId).execute().body();

            if (gitLabProject != null) {
                return Optional.of(gitLabProject);
            } else {
                Log.error("Failed to fetch GitLab project with id={}", projectId);
                return Optional.empty();
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
