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

package org.varys.common.model;

import org.pmw.tinylog.Level;

public class LoggingConfig {

    private final String loggingDirectory;
    private final Level loggingLevel;

    public LoggingConfig(String loggingDirectory, String level) {
        this.loggingDirectory = loggingDirectory;
        this.loggingLevel = Level.valueOf(level.toUpperCase());
    }

    public String getLoggingDirectory() {
        return loggingDirectory;
    }

    public Level getLoggingLevel() {
        return loggingLevel;
    }
}
