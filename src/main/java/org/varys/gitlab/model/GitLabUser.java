package org.varys.gitlab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabUser {

    static final GitLabUser UNASSIGNED =
            new GitLabUser(-1, "Unassigned");

    private final long id;
    private final String name;

    GitLabUser() {
        this.id = -1;
        this.name = null;
    }

    public GitLabUser(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitLabUser that = (GitLabUser) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "GitLabUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
