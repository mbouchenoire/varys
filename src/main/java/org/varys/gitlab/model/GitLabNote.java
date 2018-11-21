package org.varys.gitlab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.beans.Transient;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabNote {

    private final long id;
    private final String body;

    GitLabNote() {
        this.id = -1;
        this.body = null;
    }

    public GitLabNote(long id, String body) {
        this.id = id;
        this.body = body;
    }

    public long getId() {
        return id;
    }

    @Transient
    boolean isAutomaticComment() {
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
                '}';
    }
}
