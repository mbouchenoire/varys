package org.varys.gitlab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabUser {

    public static final GitLabUser UNASSIGNED =
            new GitLabUser(-1, "Unassigned", "Unassigned", "", "");

    private final long id;
    private final String name;
    private final String username;
    @JsonProperty("avatar_url")
    private final String avatarUrl;
    @JsonProperty("web_url")
    private final String url;

    GitLabUser() {
        this.id = -1;
        this.name = null;
        this.username = null;
        this.avatarUrl = null;
        this.url = null;
    }

    private GitLabUser(long id, String name, String username, String avatarUrl, String url) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getUrl() {
        return url;
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
                ", username='" + username + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
