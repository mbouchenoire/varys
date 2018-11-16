package org.varys.gitlab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown =  true)
public class GitLabProject {

    private final long id;
    @JsonProperty("path_with_namespace")
    private final String pathWithNamespace;

    GitLabProject() {
        this.id = -1;
        this.pathWithNamespace = null;
    }

    public long getId() {
        return id;
    }

    public String getPathWithNamespace() {
        return pathWithNamespace;
    }

    public String formatMergeRequest(MergeRequest mergeRequest) {
        return String.format("%s!%d", this.pathWithNamespace, mergeRequest.getIid());
    }

    @Override
    public String toString() {
        return "GitLabProject{" +
                "id=" + id +
                ", pathWithNamespace='" + pathWithNamespace + '\'' +
                '}';
    }
}
