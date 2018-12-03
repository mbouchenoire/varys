package org.varys.gitlab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.beans.Transient;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabNote {

    private final long id;
    private final String body;
    @JsonProperty("created_at")
    private final Date createdAt;
    private final GitLabUser author;

    GitLabNote() {
        this.id = -1;
        this.body = null;
        this.createdAt = null;
        this.author = null;
    }

    public GitLabNote(long id, String body, Date createdAt, GitLabUser author) {
        this.id = id;
        this.body = body;
        this.createdAt = createdAt;
        this.author = author;
    }

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public GitLabUser getAuthor() {
        return author;
    }

    @Transient
    boolean isAutomaticComment() {
        if (body == null) {
            return true;
        }

        return body.startsWith("added")
                || body.startsWith("reopened")
                || body.startsWith("closed")
                || body.startsWith("merged")
                || body.startsWith("changed")
                || body.startsWith("marked");
    }

    @Override
    public String toString() {
        return "GitLabNote{" +
                "id=" + id +
                ", body='" + body + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
