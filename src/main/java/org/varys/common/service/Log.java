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

package org.varys.common.service;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.policies.DailyPolicy;
import org.pmw.tinylog.policies.SizePolicy;
import org.pmw.tinylog.writers.RollingFileWriter;
import org.varys.common.model.LoggingConfig;

public final class Log {

    private Log() {
        super();
    }

    public static void init(LoggingConfig config) {
        final RollingFileWriter fileWriter = new RollingFileWriter(
                config.getLoggingDirectory(),
                5,
                new SizePolicy(5 * 1024 * 1024),
                new DailyPolicy(0, 0));

        Configurator.defaultConfig()
                .addWriter(fileWriter)
                .formatPattern("{date:yyyy-MM-dd HH:mm:ss} {level}: {message}")
                .level(config.getLoggingLevel())
                .activate();
    }

    public static void trace(String message) {
        Logger.trace(message);
    }

    public static void trace(String message, Object... argumengs) {
        Logger.trace(message, argumengs);
    }

    public static void trace(Exception e, String message, Object ... arguments) {
        Logger.trace(e, message, arguments);
    }

    public static void debug(String message) {
        Logger.debug(message);
    }

    public static void debug(String message, Object... argumengs) {
        Logger.debug(message, argumengs);
    }

    public static void debug(Throwable t, String message, Object ... arguments) {
        Logger.debug(t, message, arguments);
    }

    public static void info(String message) {
        Logger.info(message);
    }

    public static void info(String message, Object ... arguments) {
        Logger.info(message, arguments);
    }

    public static void warn(String message) {
        Logger.warn(message);
    }

    public static void warn(String message,  Object ... arguments) {
        Logger.warn(message, arguments);
    }

    public static void error(String message) {
        Logger.error(message);
    }

    public static void error(Throwable t, String message) {
        Logger.error(t, message);
    }

    public static void error(Throwable t, String message, Object ... arguments) {
        Logger.error(t, message, arguments);
    }

    public static void error(String message, Object ... arguments) {
        Logger.error(message, arguments);
    }
}
