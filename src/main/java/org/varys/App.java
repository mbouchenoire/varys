package org.varys;

import com.fasterxml.jackson.databind.JsonNode;
import org.varys.common.model.GitConfig;
import org.varys.common.model.LoggingConfig;
import org.varys.common.model.StartupNotification;
import org.varys.common.service.ConfigFactory;
import org.varys.common.service.Log;
import org.varys.common.service.NotificationService;
import org.varys.common.service.NotifierModule;
import org.varys.common.service.NotifierModuleFactory;
import org.varys.common.service.VarysTrayIcon;

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
        Log.init(loggingConfig);

        VarysTrayIcon.addPermanentClickableMenu("Varys - Stop", App::stop);
        VarysTrayIcon.addPermanentClickableMenu("Varys - Open application folder", () -> openAppFolder(configFile));
        VarysTrayIcon.addPermanentClickableMenu("Varys - Edit configuration", () -> editConfiguration(configFile));

        final int threadPoolSize = ConfigFactory.getThreadPoolSize(configFile);

        final GitConfig gitConfig = ConfigFactory.createGitConfig(configFile);
        final NotifierModuleFactory notifierModuleFactory = new NotifierModuleFactory(gitConfig);
        final Collection<JsonNode> moduleNodes = ConfigFactory.findModuleNodes(configFile);
        final Collection<NotifierModule> notifierModules = notifierModuleFactory.createAll(moduleNodes);

        new NotificationService("varys").send(new StartupNotification());

        final ForkJoinPool forkJoinPool = new ForkJoinPool(
                threadPoolSize,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                (t, e) -> Log.error(e, "Uncaught exception"),
                false);

        Log.info("Starting {} module(s)...", notifierModules.size());
        forkJoinPool.invokeAll(notifierModules);

        final int input = System.in.read();
        Log.info("Varys is shutting down (input: {})...", input);
    }

    private static void stop() {
        Log.info("Stopping Varys (user request)...");
        System.exit(0);
    }

    private static void openAppFolder(File configFile) {
        Log.info("Opening application folder (user request)...");

        final File[] binFolders = configFile.getParentFile().getParentFile().listFiles((dir, name) -> name.equals("bin"));

        try {
            if (binFolders != null) {
                final File binFolder = binFolders[0];
                Desktop.getDesktop().open(binFolder);
            } else {
                throw new IOException("Could not find application folder");
            }
        } catch (IOException e) {
            Log.error(e, "Failed to open application folder");
        }
    }

    private static void editConfiguration(File configFile) {
        Log.info("Opening configuration file (user request)...");

        try {
            Desktop.getDesktop().open(configFile);
        } catch (IOException e) {
            Log.error(e, "Failed to open configuration file");
        }
    }
}
