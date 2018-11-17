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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;

public class App {

    private static final int FORK_JOIN_POOL_SIZE = 50;

    public static void main(String[] args) throws IOException {
        final String configFilePath = args[0];
        final File configFile = new File(configFilePath);

        final LoggingConfig loggingConfig = ConfigFactory.createLoggingConfig(configFile);
        Log.init(loggingConfig);

        final GitConfig gitConfig = ConfigFactory.createGitConfig(configFile);
        final NotifierModuleFactory notifierModuleFactory = new NotifierModuleFactory(gitConfig);
        final Collection<JsonNode> moduleNodes = ConfigFactory.findModuleNodes(configFile);
        final Collection<NotifierModule> notifierModules = notifierModuleFactory.createAll(moduleNodes);

        new NotificationService("varys").send(new StartupNotification());

        final ForkJoinPool forkJoinPool = new ForkJoinPool(FORK_JOIN_POOL_SIZE);
        forkJoinPool.invokeAll(notifierModules);
    }
}
