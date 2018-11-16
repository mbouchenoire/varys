package org.varys.common.model;

public class GitConfig {

    private final String parentDirectory;

    public GitConfig() {
        super();
        this.parentDirectory = null;
    }

    public GitConfig(String parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    public String getParentDirectory() {
        return parentDirectory;
    }
}
