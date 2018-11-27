package org.varys.common.model;

import java.io.File;

public class GitConfig {

    private final File parentDirectory;

    public GitConfig() {
        super();
        this.parentDirectory = null;
    }

    public GitConfig(File parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    public File getParentDirectory() {
        return parentDirectory;
    }
}
