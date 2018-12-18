package org.varys.gitlab.model;

import org.varys.common.model.exception.ConfigurationException;

public class UnsupportedGitLabApiVersionException extends ConfigurationException {

    public UnsupportedGitLabApiVersionException() {
        super("Cannot find compatible GitLab API version");
    }
}
