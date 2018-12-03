package org.varys.gitlab.model;

import java.util.Optional;

public class GitLabMergeRequestTask {

    private final String description;
    private final boolean completed;
    private final GitLabUser completor;

    GitLabMergeRequestTask(String description, boolean completed, GitLabUser completor) {
        this.description = description;
        this.completed = completed;
        this.completor = completor;
    }

    GitLabMergeRequestTask(String description, boolean completed) {
        this(description, completed, null);
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Optional<GitLabUser> getCompletor() {
        return Optional.ofNullable(completor);
    }
}
