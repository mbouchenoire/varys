package org.varys.gitlab.model;

import org.varys.common.model.Linkable;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GitLabMergeRequest implements MergeRequest, Linkable {

    private final long id;
    private final long iid;
    private final GitLabProject project;
    private final String title;
    private final GitLabMergeRequestState state;
    private final String targetBranch;
    private final String sourceBranch;
    private final GitLabUser author;
    private final GitLabUser assignee;
    private final List<GitLabNote> notes;
    private final List<GitLabCommit> commits;
    private final String url;

    GitLabMergeRequest() {
        this.id = -1;
        this.iid = -1;
        this.project = null;
        this.title = null;
        this.state = null;
        this.targetBranch = null;
        this.sourceBranch = null;
        this.author = null;
        this.assignee = null;
        this.notes = new ArrayList<>();
        this.commits = new ArrayList<>();
        this.url = null;
    }

    public GitLabMergeRequest(
            GitLabMergeRequestListItem mergeRequestListItem,
            GitLabProject project,
            List<GitLabNote> notes,
            List<GitLabCommit> commits) {

        this.id = mergeRequestListItem.getId();
        this.iid = mergeRequestListItem.getIid();
        this.project = project;
        this.title = mergeRequestListItem.getTitle();
        this.state = mergeRequestListItem.getState();
        this.targetBranch = mergeRequestListItem.getTargetBranch();
        this.sourceBranch = mergeRequestListItem.getSourceBranch();
        this.author = mergeRequestListItem.getAuthor();
        this.assignee = mergeRequestListItem.getAssignee();
        this.notes = Collections.unmodifiableList(notes);
        this.commits = Collections.unmodifiableList(commits);
        this.url = mergeRequestListItem.getUrl();
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

    public GitLabUser getAssignee() {
        return assignee;
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
                ", targetBranch='" + targetBranch + '\'' +
                ", sourceBranch='" + sourceBranch + '\'' +
                ", author=" + author +
                ", assignee=" + assignee +
                ", notes=" + notes +
                ", commits=" + commits +
                ", url='" + url + '\'' +
                '}';
    }
}
