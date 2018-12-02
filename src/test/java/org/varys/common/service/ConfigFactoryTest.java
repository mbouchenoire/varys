package org.varys.common.service;

import org.junit.Test;
import org.pmw.tinylog.Level;
import org.varys.common.model.GitConfig;
import org.varys.common.model.LoggingConfig;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConfigFactoryTest {

    private final File configFile;

    public ConfigFactoryTest() {
        super();
        final ClassLoader classLoader = getClass().getClassLoader();
        this.configFile = new File(classLoader.getResource("config.yml").getFile());
    }

    @Test
    public void getThreadPoolSize() throws IOException {
        assertEquals(Runtime.getRuntime().availableProcessors(), ConfigFactory.getThreadPoolSize(this.configFile));
    }

    @Test
    public void createLoggingConfig() throws IOException {
        final LoggingConfig loggingConfig = ConfigFactory.createLoggingConfig(this.configFile);
        assertEquals(Level.INFO, loggingConfig.getLoggingLevel());
        assertEquals("varys.log", loggingConfig.getLoggingDirectory());
    }

    @Test
    public void createGitConfig() throws IOException {
        final GitConfig gitConfig = ConfigFactory.createGitConfig(this.configFile);
        final String configuredGitRepositoryPath = File.separator + "home" + File.separator + "git";
        assertTrue(gitConfig.getParentDirectory().getAbsolutePath().endsWith(configuredGitRepositoryPath));
    }

    @Test
    public void findModuleNodes() throws IOException {
        assertEquals(2, ConfigFactory.findModuleNodes(this.configFile).size());
    }
}