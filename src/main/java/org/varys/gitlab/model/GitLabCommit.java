package org.varys.gitlab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabCommit {

    private final String id;

    GitLabCommit() {
        this.id = null;
    }

    public GitLabCommit(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "GitLabCommit{" +
                "id='" + id + '\'' +
                '}';
    }
}
