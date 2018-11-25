package org.varys.gitlab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabMergeRequestListItem implements MergeRequest {

    private final long id;
    private final long iid;
    @JsonProperty("project_id")
    private final long projectId;
    private final String title;
    private final GitLabMergeRequestState state;
    @JsonProperty("updated_at")
    private final Date updatedAt;
    @JsonProperty("target_branch")
    private final String targetBranch;
    @JsonProperty("source_branch")
    private final String sourceBranch;
    private final GitLabUser author;
    private final GitLabUser assignee;
    @JsonProperty("user_notes_count")
    private final long userNotesCount;
    @JsonProperty("web_url")
    private final String url;

    GitLabMergeRequestListItem() {
        this.id = -1;
        this.iid = -1;
        this.projectId = -1;
        this.title = null;
        this.state = null;
        this.updatedAt = null;
        this.targetBranch = null;
        this.sourceBranch = null;
        this.author = null;
        this.assignee = GitLabUser.UNASSIGNED;
        this.userNotesCount = -1;
        this.url = null;
    }

    public GitLabMergeRequestListItem(
            long id,
            long iid,
            long projectId,
            String title,
            GitLabMergeRequestState state,
            Date updatedAt,
            String targetBranch,
            String sourceBranch,
            GitLabUser author,
            GitLabUser assignee,
            long userNotesCount,
            String url) {

        this.id = id;
        this.iid = iid;
        this.projectId = projectId;
        this.title = title;
        this.state = state;
        this.updatedAt = updatedAt;
        this.targetBranch = targetBranch;
        this.sourceBranch = sourceBranch;
        this.author = author;
        this.assignee = assignee;
        this.userNotesCount = userNotesCount;
        this.url = url;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public long getIid() {
        return iid;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    public GitLabMergeRequestState getState() {
        return state;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    public String getSourceBranch() {
        return sourceBranch;
    }

    public GitLabUser getAuthor() {
        return author;
    }

    public GitLabUser getAssignee() {
        return assignee;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "GitLabMergeRequestListItem{" +
                "id=" + id +
                ", iid=" + iid +
                ", projectId=" + projectId +
                ", title='" + title + '\'' +
                ", state=" + state +
                ", targetBranch='" + targetBranch + '\'' +
                ", sourceBranch='" + sourceBranch + '\'' +
                ", author=" + author +
                ", assignee=" + assignee +
                ", userNotesCount=" + userNotesCount +
                ", url='" + url + '\'' +
                '}';
    }
}
