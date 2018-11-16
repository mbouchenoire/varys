package org.varys.common.service;

import org.pmw.tinylog.policies.DailyPolicy;
import org.varys.common.model.LoggingConfig;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.policies.SizePolicy;
import org.pmw.tinylog.writers.RollingFileWriter;

public class Log {

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

    public static void debug(String message) {
        Logger.debug(message);
    }

    public static void debug(String message, Object... argumengs) {
        Logger.debug(message, argumengs);
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
