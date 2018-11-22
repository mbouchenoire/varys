package org.varys.gitlab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown =  true)
public class GitLabProject {

    private final long id;
    private final String name;
    @JsonProperty("path_with_namespace")
    private final String pathWithNamespace;

    GitLabProject() {
        this.id = -1;
        this.name = null;
        this.pathWithNamespace = null;
    }

    public GitLabProject(long id, String name, String pathWithNamespace) {
        this.id = id;
        this.name = name;
        this.pathWithNamespace = pathWithNamespace;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPathWithNamespace() {
        return pathWithNamespace;
    }

    @Override
    public String toString() {
        return "GitLabProject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pathWithNamespace='" + pathWithNamespace + '\'' +
                '}';
    }
}
