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

import org.varys.common.model.Linkable;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class GitLabMergeRequest implements MergeRequest, Linkable {

    private final long id;
    private final long iid;
    private final GitLabProject project;
    private final String title;
    private final String description;
    private final GitLabMergeRequestState state;
    private final GitLabMergeStatus mergeStatus;
    private final boolean wip;
    private final Date updatedAt;
    private final String targetBranch;
    private final String sourceBranch;
    private final GitLabUser author;
    private final GitLabUser assignee;
    private final List<GitLabNote> notes;
    private final List<GitLabCommit> commits;
    private final String url;
    private final Date lastNotificationDate;

    GitLabMergeRequest() {
        this.id = -1;
        this.iid = -1;
        this.project = null;
        this.title = null;
        this.description = null;
        this.state = null;
        this.mergeStatus = null;
        this.wip = false;
        this.updatedAt = null;
        this.targetBranch = null;
        this.sourceBranch = null;
        this.author = null;
        this.assignee = null;
        this.notes = new ArrayList<>();
        this.commits = new ArrayList<>();
        this.url = null;
        this.lastNotificationDate = null;
    }

    private GitLabMergeRequest(
            long id,
            long iid,
            GitLabProject project,
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
            List<GitLabNote> notes,
            List<GitLabCommit> commits,
            String url,
            Date lastNotificationDate) {

        this.id = id;
        this.iid = iid;
        this.project = project;
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
        this.notes = notes;
        this.commits = commits;
        this.url = url;
        this.lastNotificationDate = lastNotificationDate;
    }

    public GitLabMergeRequest(
            GitLabMergeRequestListItem mergeRequestListItem,
            GitLabProject project,
            List<GitLabNote> notes,
            List<GitLabCommit> commits) {

        this(mergeRequestListItem.getIid(),
                mergeRequestListItem.getIid(),
                project,
                mergeRequestListItem.getTitle(),
                mergeRequestListItem.getDescription(),
                mergeRequestListItem.getState(),
                mergeRequestListItem.getMergeStatus(),
                mergeRequestListItem.isWip(),
                mergeRequestListItem.getUpdatedAt(),
                mergeRequestListItem.getTargetBranch(),
                mergeRequestListItem.getSourceBranch(),
                mergeRequestListItem.getAuthor(),
                mergeRequestListItem.getAssignee(),
                notes,
                commits,
                mergeRequestListItem.getUrl(),
                null);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public long getIid() {
        return iid;
    }

    public GitLabProject getProject() {
        return project;
    }

    public String getTitle() {
        return title;
    }

    public GitLabMergeRequestState getState() {
        return state;
    }

    public GitLabMergeStatus getMergeStatus() {
        return mergeStatus;
    }

    @Transient
    public boolean hasConflict() {
        return mergeStatus != null && mergeStatus.equals(GitLabMergeStatus.CANNOT_BE_MERGED);
    }

    public boolean isWip() {
        return wip;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Transient
    public boolean isDone() {
        return this.isMerged() || this.isClosed();
    }

    @Transient
    public boolean isMerged() {
        return this.getState().equals(GitLabMergeRequestState.MERGED);
    }

    @Transient
    public boolean isClosed() {
        return this.getState().equals(GitLabMergeRequestState.CLOSED);
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

    @SuppressWarnings("unused")
    public GitLabUser getAssignee() {
        return assignee;
    }

    @Transient
    public Optional<GitLabUser> getOptionalAssignee() {
        return Optional.ofNullable(this.assignee);
    }

    @Transient
    public boolean isRelevantUser(GitLabUser user) {
        return getOptionalAssignee().map(assignee -> assignee.equals(user)).orElse(author.equals(user));
    }

    public boolean sameAssignee(GitLabMergeRequest mergeRequest) {
        return Objects.equals(this.assignee, mergeRequest.assignee);
    }

    public long addedCommitsCount(GitLabMergeRequest other) {
        return this.getCommits().stream()
                .filter(commit -> !other.containsCommit(commit))
                .count();
    }

    private boolean containsCommit(GitLabCommit otherCommit) {
        return this.getCommits().stream()
                .anyMatch(commit -> commit.getId().equals(otherCommit.getId()));
    }

    public long addedUserNotesCount(GitLabMergeRequest other) {
        return this.getUserNotes().stream()
                .filter(userNote -> !other.containsUserNote(userNote))
                .count();
    }

    public long completedTasksSince(GitLabMergeRequest other) {
        final long freshCompletedCount = this.getTasks().stream().filter(GitLabMergeRequestTask::isCompleted).count();
        final long previousCompletedCount = other.getTasks().stream().filter(GitLabMergeRequestTask::isCompleted).count();
        return freshCompletedCount - previousCompletedCount;
    }

    private boolean containsUserNote(GitLabNote note) {
        return this.getUserNotes().stream()
                .anyMatch(userNote -> userNote.getId() == note.getId());
    }

    public List<GitLabNote> getNotes() {
        return new ArrayList<>(notes);
    }

    @Transient
    private List<GitLabNote> getUserNotes() {
        return notes.stream()
                .filter(note -> !note.isAutomaticComment())
                .collect(Collectors.toList());
    }

    public List<GitLabCommit> getCommits() {
        return new ArrayList<>(commits);
    }

    public String getUrl() {
        return url;
    }

    public Date getLastNotificationDate() {
        return lastNotificationDate;
    }

    @Transient
    public List<GitLabMergeRequestTask> getTasks() {
        if (this.description == null) {
            return Collections.emptyList();
        }

        // note: this algorithm does not take into account badly formated descriptions
        return Arrays.stream(this.description.split("(- \\[)|(\\\\r\\\\n)"))
                .filter(segment -> segment.startsWith("x] ") || segment.startsWith(" ] "))
                .map(segment -> {
                    final String description = segment
                            .replaceAll("x] ", "")
                            .replaceAll(" ] ", "");

                    final boolean completed = segment.startsWith("x] ");

                    return this.notes.stream()
                            .filter(note -> note.getBody().startsWith("marked the task **" + description))
                            .min((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()))
                            .map(GitLabNote::getAuthor)
                            .map(completor -> new GitLabMergeRequestTask(description, completed, completor))
                            .orElse(new GitLabMergeRequestTask(description, completed));
                })
                .collect(Collectors.toList());
    }

    public GitLabMergeRequest notified() {
        return new GitLabMergeRequest(
                this.id,
                this.iid,
                this.project,
                this.title,
                this.description,
                this.state,
                this.mergeStatus,
                this.wip,
                this.updatedAt,
                this.targetBranch,
                this.sourceBranch,
                this.author,
                this.assignee,
                this.notes,
                this.commits,
                this.url,
                new Date()
        );
    }

    @Transient
    @Override
    public String getLabel() {
        return "GitLab - " + this.getProject().getPathWithNamespace() + " - " + this.title;
    }

    @Transient
    public String getIdentifier() {
        return String.format("%s!%d", this.project.getPathWithNamespace(), this.getIid());
    }

    @Override
    public String toString() {
        return "GitLabMergeRequest{" +
                "id=" + id +
                ", iid=" + iid +
                ", project=" + project +
                ", title='" + title + '\'' +
                ", state=" + state +
                ", mergeStatus=" + mergeStatus +
                ", wip=" + wip +
                ", targetBranch='" + targetBranch + '\'' +
                ", sourceBranch='" + sourceBranch + '\'' +
                ", author=" + author +
                ", assignee=" + assignee +
                ", notes=" + notes.size() +
                ", commits=" + commits.size()+
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitLabMergeRequest that = (GitLabMergeRequest) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
