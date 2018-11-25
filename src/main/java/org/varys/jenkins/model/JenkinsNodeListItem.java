package org.varys.jenkins.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JenkinsNodeListItem {

    private final String name;
    @JsonProperty("url")
    private final String url;

    JenkinsNodeListItem() {
        this.name = null;
        this.url = null;
    }

    public JenkinsNodeListItem(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getApiUrl() {
        return (this.url + "/api/json").replace("//", "/").replace(":/", "://");
    }

    @Override
    public String toString() {
        return "JenkinsNodeListItem{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
