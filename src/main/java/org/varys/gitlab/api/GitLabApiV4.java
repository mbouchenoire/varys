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

    GitLabApiV4(GitLabApiConfig apiConfig) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiConfig.getBaseUrl())
                .client(OkHttpClientFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        this.gitLabApiV4Retrofit = retrofit.create(GitLabApiV4Retrofit.class);
        this.apiConfig = apiConfig;
    }

    @Override
    public String getBaseUrl() {
        return this.apiConfig.getBaseUrl();
    }

    @Override
    public List<GitLabMergeRequestDetails> getMergeRequests(GitLabMergeRequestState state) {
        try {
            final List<GitLabMergeRequestListItem> listItems = this.gitLabApiV4Retrofit.getMergeRequests(
                    this.apiConfig.getPrivateToken(), state.getCode()).execute().body();

            assert listItems != null;

            Log.debug("Fetched {} GitLab merge request(s) (opened)", listItems.size());

            return listItems.parallelStream()
                    .map(this::fetchDetails)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            Log.error(e, "Failed to retreive GitLab merge requests");
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<GitLabMergeRequestDetails> getMergeRequest(long projectId, long mergeRequestId, long mergeRequestIid) {
        try {
            final GitLabMergeRequestListItem mergeRequestListItem = this.gitLabApiV4Retrofit.getMergeRequest(
                    this.apiConfig.getPrivateToken(), projectId, mergeRequestIid).execute().body();

            if (mergeRequestListItem != null) {
                return this.fetchDetails(mergeRequestListItem);
            } else {
                Log.error("Failed to fetch GitLab merge request with iid={} within project with id={}",
                        mergeRequestIid, projectId);

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
        final long mergeRequestIid = mergeRequest.getIid();

        return this.getProject(projectId)
                .map(project -> {
                    final List<GitLabNote> notes = this.getNotes(projectId, mergeRequestIid);
                    final List<GitLabCommit> commits = this.getCommits(projectId, mergeRequestIid);
                    return new GitLabMergeRequestDetails(mergeRequest, project, notes, commits);
                });
    }

    private Optional<GitLabProject> getProject(long projectId) {
        try {
            final GitLabProject gitLabProject = this.gitLabApiV4Retrofit.getProject(
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

    private List<GitLabNote> getNotes(long projectId, long mergeRequestIid) {
        try {
            return this.gitLabApiV4Retrofit.getNotes(
                    this.apiConfig.getPrivateToken(), projectId, mergeRequestIid).execute().body();
        } catch (IOException e) {
            Log.error(e, "Failed to fetch GitLab notes for merge request with project_id={} and iid={}",
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
            Log.error(e, "Failed to fetch GitLab commits for merge request with project_id={} and iid={}",
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