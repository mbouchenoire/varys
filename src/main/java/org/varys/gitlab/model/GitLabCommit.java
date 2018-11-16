package org.varys.gitlab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabCommit {

    private final String id;
    private final String message;
    @JsonProperty("author_name")
    private final String authorName;

    GitLabCommit() {
        this.id = null;
        this.message = null;
        this.authorName = null;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthorName() {
        return authorName;
    }

    @Override
    public String toString() {
        return "GitLabCommit{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", authorName='" + authorName + '\'' +
                '}';
    }
}
