package org.varys.gitlab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabProjectListItem {

    private final String id;

    GitLabProjectListItem() {
        this.id = null;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "GitLabProjectListItem{" +
                "id='" + id + '\'' +
                '}';
    }
}
