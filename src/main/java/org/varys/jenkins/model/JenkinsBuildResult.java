package org.varys.jenkins.model;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public enum JenkinsBuildResult {
    SUCCESS("SUCCESS", "Successful", TrayIcon.MessageType.INFO),
    FAILURE("FAILURE", "Failed", TrayIcon.MessageType.ERROR),
    ABORTED("ABORTED", "Aborted", TrayIcon.MessageType.WARNING),
    UNSTABLE("UNSTABLE", "Unstable", TrayIcon.MessageType.WARNING),
    NOT_BUILT("NOT_BUILT", "Not built", TrayIcon.MessageType.WARNING),
    NONE(null, "?", TrayIcon.MessageType.NONE);

    private final String code;
    private final String adjective;
    private final TrayIcon.MessageType messageType;

    JenkinsBuildResult(String code, String adjective, TrayIcon.MessageType messageType) {
        this.code = code;
        this.adjective = adjective;
        this.messageType = messageType;
    }

    static JenkinsBuildResult of(String code) {
        return Arrays.stream(values())
                .filter(result -> Objects.equals(result.code, code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Jenkins build result: " + code));
    }

    public String getAdjective() {
        return adjective;
    }

    public TrayIcon.MessageType getMessageType() {
        return messageType;
    }
}
