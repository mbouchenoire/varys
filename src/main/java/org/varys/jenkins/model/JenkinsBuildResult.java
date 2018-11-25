package org.varys.jenkins.model;

import org.varys.common.model.NotificationType;

import java.util.Arrays;
import java.util.Objects;

public enum JenkinsBuildResult {
    SUCCESS("SUCCESS", "Successful", NotificationType.INFO),
    FAILURE("FAILURE", "Failed", NotificationType.ERROR),
    ABORTED("ABORTED", "Aborted", NotificationType.WARNING),
    UNSTABLE("UNSTABLE", "Unstable", NotificationType.WARNING),
    NOT_BUILT("NOT_BUILT", "Not built", NotificationType.WARNING),
    NONE(null, "?", NotificationType.NONE);

    private final String code;
    private final String adjective;
    private final NotificationType notificationType;

    JenkinsBuildResult(String code, String adjective, NotificationType notificationType) {
        this.code = code;
        this.adjective = adjective;
        this.notificationType = notificationType;
    }

    public String getCode() {
        return code;
    }

    static JenkinsBuildResult ofCode(String code) {
        return Arrays.stream(values())
                .filter(result -> Objects.equals(result.code, code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Jenkins build result: " + code));
    }

    public String getAdjective() {
        return adjective;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }
}
