package org.varys.gitlab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabVersion {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitLabVersion.class);

    private final String version;

    GitLabVersion() {
        super();
        this.version = null;
    }

    public GitLabVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public int getMajor() {
        if (this.version == null) {
            LOGGER.warn("Cannot parse GitLab major version (null version)");
            return -1;
        }

        final String majorString = version.split("\\.")[0];
        return Integer.parseInt(majorString);
    }

    public int getMinor() {
        if (this.version == null) {
            LOGGER.warn("Cannot parse GitLab minor version (null version)");
            return -1;
        }

        final String majorString = version.split("\\.")[1];
        return Integer.parseInt(majorString);
    }

    @Override
    public String toString() {
        return this.version;
    }
}
