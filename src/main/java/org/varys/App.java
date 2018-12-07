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

package org.varys;

import com.fasterxml.jackson.databind.JsonNode;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.policies.DailyPolicy;
import org.pmw.tinylog.policies.SizePolicy;
import org.pmw.tinylog.writers.RollingFileWriter;
import org.varys.common.model.GitConfig;
import org.varys.common.model.LoggingConfig;
import org.varys.common.model.StartupNotification;
import org.varys.common.service.ConfigFactory;
import org.varys.common.service.NotificationService;
import org.varys.common.service.NotifierModule;
import org.varys.common.service.NotifierModuleFactory;
import org.varys.common.service.VarysTrayIcon;
import org.varys.gitlab.api.GitLabApiLocator;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;

public class App {

    public static void main(String[] args) throws IOException {
        final String configFilePath = args[0];
        final File configFile = new File(configFilePath);

        final LoggingConfig loggingConfig = ConfigFactory.createLoggingConfig(configFile);
        initLogConfig(loggingConfig);

        VarysTrayIcon.addPermanentClickableMenu("Varys - Stop", App::stop);
        VarysTrayIcon.addPermanentClickableMenu("Varys - Open application folder", () -> openAppFolder(configFile));
        VarysTrayIcon.addPermanentClickableMenu("Varys - Edit configuration", () -> editConfiguration(configFile));

        final int threadPoolSize = ConfigFactory.getThreadPoolSize(configFile);

        final GitConfig gitConfig = ConfigFactory.createGitConfig(configFile);
        final GitLabApiLocator gitLabApiLocator = new GitLabApiLocator();
        final NotifierModuleFactory notifierModuleFactory = new NotifierModuleFactory(gitConfig, gitLabApiLocator);
        final Collection<JsonNode> moduleNodes = ConfigFactory.findModuleNodes(configFile);
        final Collection<NotifierModule> notifierModules = notifierModuleFactory.createAll(moduleNodes);

        new NotificationService("varys").send(new StartupNotification(notifierModules));

        final ForkJoinPool forkJoinPool = new ForkJoinPool(
                threadPoolSize,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                (t, e) -> Logger.error(e, "Uncaught exception"),
                false);

        Logger.info("Starting {} module(s)...", notifierModules.size());
        forkJoinPool.invokeAll(notifierModules);

        final int input = System.in.read();
        Logger.info("Varys is shutting down (input: {})...", input);
    }

    private static void stop() {
        Logger.info("Stopping Varys (user request)...");
        System.exit(0);
    }

    private static void openAppFolder(File configFile) {
        Logger.info("Opening application folder (user request)...");

        final File[] binFolders = configFile.getParentFile().getParentFile().listFiles((dir, name) -> name.equals("bin"));

        try {
            if (binFolders != null) {
                final File binFolder = binFolders[0];
                Desktop.getDesktop().open(binFolder);
            } else {
                throw new IOException("Could not find application folder");
            }
        } catch (IOException e) {
            Logger.error(e, "Failed to open application folder");
        }
    }

    private static void editConfiguration(File configFile) {
        Logger.info("Opening configuration file (user request)...");

        try {
            Desktop.getDesktop().open(configFile);
        } catch (IOException e) {
            Logger.error(e, "Failed to open configuration file");
        }
    }

    private static void initLogConfig(LoggingConfig loggingConfig) {
        final RollingFileWriter fileWriter = new RollingFileWriter(
                loggingConfig.getLoggingDirectory(),
                5,
                new SizePolicy(5 * 1024 * 1024),
                new DailyPolicy(0, 0));

        Configurator.defaultConfig()
                .addWriter(fileWriter)
                .formatPattern("{date:yyyy-MM-dd HH:mm:ss} {level}: {message}")
                .level(loggingConfig.getLoggingLevel())
                .activate();
    }
}
