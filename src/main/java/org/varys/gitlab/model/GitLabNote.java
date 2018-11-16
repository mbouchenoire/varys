package org.varys.gitlab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.beans.Transient;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabNote {

    private final long id;
    private final String body;
    private final GitLabUser author;

    GitLabNote() {
        this.id = -1;
        this.body = null;
        this.author = null;
    }

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public GitLabUser getAuthor() {
        return author;
    }

    @Transient
    public boolean isAutomaticComment() {
        if (body == null) {
            return true;
        }

        return body.startsWith("added")
                || body.startsWith("reopened")
                || body.startsWith("closed")
                || body.startsWith("merged");
    }

    @Override
    public String toString() {
        return "GitLabNote{" +
                "id=" + id +
                ", body='" + body + '\'' +
                ", author=" + author +
                '}';
    }
}
