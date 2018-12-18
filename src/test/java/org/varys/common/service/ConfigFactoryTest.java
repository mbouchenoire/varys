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

import org.junit.Test;
import org.pmw.tinylog.Level;
import org.varys.common.model.GitConfig;
import org.varys.common.model.LoggingConfig;
import org.varys.common.model.exception.ConfigurationException;

import java.io.File;

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
    public void getThreadPoolSize() throws ConfigurationException {
        assertEquals(Runtime.getRuntime().availableProcessors(), ConfigFactory.getThreadPoolSize(this.configFile));
    }

    @Test
    public void createLoggingConfig() throws ConfigurationException {
        final LoggingConfig loggingConfig = ConfigFactory.createLoggingConfig(this.configFile);
        assertEquals(Level.INFO, loggingConfig.getLoggingLevel());
        assertEquals("varys.log", loggingConfig.getLoggingDirectory());
    }

    @Test
    public void createGitConfig() throws ConfigurationException {
        final GitConfig gitConfig = ConfigFactory.createGitConfig(this.configFile);
        final String configuredGitRepositoryPath = File.separator + "home" + File.separator + "git";
        assertTrue(gitConfig.getParentDirectory().getAbsolutePath().endsWith(configuredGitRepositoryPath));
    }

    @Test
    public void findModuleNodes() throws ConfigurationException {
        assertEquals(2, ConfigFactory.findModuleNodes(this.configFile).size());
    }
}