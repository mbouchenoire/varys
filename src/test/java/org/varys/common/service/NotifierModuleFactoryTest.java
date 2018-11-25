package org.varys.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.varys.common.model.GitConfig;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NotifierModuleFactoryTest {

    private final File configFile;

    public NotifierModuleFactoryTest() {
        super();
        final ClassLoader classLoader = getClass().getClassLoader();
        this.configFile = new File(classLoader.getResource("config.json").getFile());
    }

    @Test
    public void createAll() throws IOException {
        final GitConfig gitConfig = ConfigFactory.createGitConfig(this.configFile);
        final Collection<JsonNode> moduleNodes = ConfigFactory.findModuleNodes(configFile);

        final Collection<NotifierModule> modules = new NotifierModuleFactory(gitConfig).createAll(moduleNodes);
        assertEquals(2, modules.size());

        assertTrue(modules.stream()
                .anyMatch(module -> module.getName().equals("Jenkins") && module.getPeriodSeconds() == 30));

        assertTrue(modules.stream()
                .anyMatch(module -> module.getName().equals("GitLab") && module.getPeriodSeconds() == 30));
    }
}