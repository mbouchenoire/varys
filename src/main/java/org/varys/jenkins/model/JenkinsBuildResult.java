/*
 * This file is part of Varys.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Varys.  If not, see <https://www.gnu.org/licenses/>.
 */

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
