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
    private final String description;
    private final GitLabMergeRequestState state;
    @JsonProperty("merge_status")
    private final GitLabMergeStatus mergeStatus;
    @JsonProperty("updated_at")
    private final Date updatedAt;
    @JsonProperty("target_branch")
    private final String targetBranch;
    @JsonProperty("source_branch")
    private final String sourceBranch;
    private final GitLabUser author;
    private final GitLabUser assignee;
    @JsonProperty("work_in_progress")
    private final boolean wip;
    @JsonProperty("user_notes_count")
    private final long userNotesCount;
    @JsonProperty("web_url")
    private final String url;

    GitLabMergeRequestListItem() {
        this.id = -1;
        this.iid = -1;
        this.projectId = -1;
        this.title = null;
        this.description = null;
        this.state = null;
        this.mergeStatus = null;
        this.wip = false;
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
            String description,
            GitLabMergeRequestState state,
            GitLabMergeStatus mergeStatus,
            boolean wip,
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
        this.description = description;
        this.state = state;
        this.mergeStatus = mergeStatus;
        this.wip = wip;
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

    public String getDescription() {
        return description;
    }

    public GitLabMergeRequestState getState() {
        return state;
    }

    public GitLabMergeStatus getMergeStatus() {
        return mergeStatus;
    }

    public boolean isWip() {
        return wip;
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
                ", wip=" + wip +
                ", targetBranch='" + targetBranch + '\'' +
                ", sourceBranch='" + sourceBranch + '\'' +
                ", author=" + author +
                ", assignee=" + assignee +
                ", userNotesCount=" + userNotesCount +
                ", url='" + url + '\'' +
                '}';
    }
}
