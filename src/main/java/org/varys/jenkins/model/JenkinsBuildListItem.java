package org.varys.jenkins.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.beans.Transient;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JenkinsBuildListItem implements JenkinsBuildNumber {

    private final long number;
    @JsonProperty("url")
    private final String url;

    JenkinsBuildListItem() {
        this.number = -1;
        this.url = null;
    }

    public JenkinsBuildListItem(long number, String url) {
        this.number = number;
        this.url = url;
    }

    @Override
    public long getNumber() {
        return number;
    }

    @Transient
    public String getApiUrl() {
        return (this.url + "/api/json").replace("//", "/").replace(":/", "://");
    }

    @Override
    public String toString() {
        return "JenkinsBuildListItem{" +
                "number=" + number +
                ", url='" + url + '\'' +
                '}';
    }
}
