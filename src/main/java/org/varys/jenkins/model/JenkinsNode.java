package org.varys.jenkins.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JenkinsNode {

    private final String name;
    private final String displayName;
    @JsonProperty("jobs")
    private final List<JenkinsNodeListItem> children;
    private final List<JenkinsBuildListItem> builds;

    JenkinsNode() {
        this.name = null;
        this.displayName = null;
        this.children = Collections.emptyList();
        this.builds = Collections.emptyList();
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<JenkinsNodeListItem> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public List<JenkinsBuildListItem> getBuilds() {
        return Collections.unmodifiableList(builds);
    }

    @Override
    public String toString() {
        return "JenkinsNode{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", children=" + children +
                ", builds=" + builds +
                '}';
    }
}
