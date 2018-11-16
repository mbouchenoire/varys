package org.varys.common.model;

import org.pmw.tinylog.Level;

public class LoggingConfig {

    private final String loggingDirectory;
    private final Level loggingLevel;

    public LoggingConfig(String loggingDirectory, String level) {
        this.loggingDirectory = loggingDirectory;
        this.loggingLevel = Level.valueOf(level);
    }

    public String getLoggingDirectory() {
        return loggingDirectory;
    }

    public Level getLoggingLevel() {
        return loggingLevel;
    }
}
