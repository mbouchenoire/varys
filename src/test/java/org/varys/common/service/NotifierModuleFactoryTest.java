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

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.varys.common.model.GitConfig;
import org.varys.common.model.exception.ConfigurationException;
import org.varys.gitlab.api.GitLabApiLocator;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotifierModuleFactoryTest {

    private final File configFile;

    public NotifierModuleFactoryTest() {
        super();
        final ClassLoader classLoader = getClass().getClassLoader();
        this.configFile = new File(classLoader.getResource("config.yml").getFile());
    }

    @Test
    public void createAll() throws ConfigurationException, MalformedURLException {
        final GitConfig gitConfig = ConfigFactory.createGitConfig(this.configFile);
        final Collection<JsonNode> moduleNodes = ConfigFactory.findModuleNodes(configFile);

        final GitLabApiLocator gitLabApiLocator = mock(GitLabApiLocator.class);
        when(gitLabApiLocator.findUsable(null, null)).thenReturn(null);

        final NotifierModuleFactory notifierModuleFactory = new NotifierModuleFactory(gitConfig, gitLabApiLocator);
        final Collection<NotifierModule> modules = notifierModuleFactory.createAll(moduleNodes);
        assertEquals(2, modules.size());

        assertTrue(modules.stream()
                .anyMatch(module -> module.getName().equals("Jenkins") && module.getPeriodSeconds() == 30));

        assertTrue(modules.stream()
                .anyMatch(module -> module.getName().equals("GitLab") && module.getPeriodSeconds() == 30));
    }
}
