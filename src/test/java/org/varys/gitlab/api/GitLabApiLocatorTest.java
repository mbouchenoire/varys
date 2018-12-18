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

package org.varys.gitlab.api;

import org.junit.Test;
import org.varys.common.model.RestApiStatus;
import org.varys.common.model.exception.BadPrivateTokenConfigurationException;
import org.varys.common.model.exception.BadSslConfigurationException;
import org.varys.common.model.exception.ConfigurationException;
import org.varys.common.model.exception.UnreachableApiConfigurationException;
import org.varys.gitlab.model.UnsupportedGitLabApiVersionException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GitLabApiLocatorTest {

    @Test
    public void findUsable() throws ConfigurationException {
        final RestApiStatus unreachable = new RestApiStatus(false, true, true, true);
        final RestApiStatus incompatible = new RestApiStatus(true, false, true, true);
        final RestApiStatus badSslConf = new RestApiStatus(true, true, false, true);
        final RestApiStatus badPrivateToken = new RestApiStatus(true, true, true, false);

        final GitLabApiLocator locator = new GitLabApiLocator();
        final GitLabApiV3 gitLabApiV3 = mock(GitLabApiV3.class);
        when(gitLabApiV3.getVersion()).thenReturn(3);
        final GitLabApiV4 gitLabApiV4 = mock(GitLabApiV4.class);
        when(gitLabApiV4.getVersion()).thenReturn(4);

        when(gitLabApiV3.getStatus()).thenReturn(unreachable);
        when(gitLabApiV4.getStatus()).thenReturn(unreachable);

        try {
            locator.findUsable(gitLabApiV3, gitLabApiV4);
            fail();
        } catch (UnreachableApiConfigurationException e) {
            assertTrue(e.getMessage().contains("reach"));
        }

        when(gitLabApiV3.getStatus()).thenReturn(incompatible);
        when(gitLabApiV4.getStatus()).thenReturn(incompatible);

        try {
            locator.findUsable(gitLabApiV3, gitLabApiV4);
            fail();
        } catch (UnsupportedGitLabApiVersionException e) {
            assertTrue(e.getMessage().contains("compatible"));
        }

        when(gitLabApiV3.getStatus()).thenReturn(badSslConf);
        when(gitLabApiV4.getStatus()).thenReturn(badSslConf);

        try {
            locator.findUsable(gitLabApiV3, gitLabApiV4);
            fail();
        } catch (BadSslConfigurationException e) {
            assertTrue(e.getMessage().contains("ssl_verify"));
        }

        when(gitLabApiV3.getStatus()).thenReturn(badPrivateToken);
        when(gitLabApiV4.getStatus()).thenReturn(badPrivateToken);

        try {
            locator.findUsable(gitLabApiV3, gitLabApiV4);
            fail();
        } catch (BadPrivateTokenConfigurationException e) {
            assertTrue(e.getMessage().contains("token"));
        }

        when(gitLabApiV3.getStatus()).thenReturn(new RestApiStatus(true, true, true, true));
        when(gitLabApiV4.getStatus()).thenReturn(new RestApiStatus(true, false, true, false));

        assertEquals(3, locator.findUsable(gitLabApiV3, gitLabApiV4).getVersion());

        when(gitLabApiV3.getStatus()).thenReturn(new RestApiStatus(true, true, true, true));
        when(gitLabApiV4.getStatus()).thenReturn(new RestApiStatus(true, false, true, false));

        assertEquals(3, locator.findUsable(gitLabApiV3, gitLabApiV4).getVersion());

        when(gitLabApiV3.getStatus()).thenReturn(new RestApiStatus(true, false, true, false));
        when(gitLabApiV4.getStatus()).thenReturn(new RestApiStatus(true, true, true, true));

        assertEquals(4, locator.findUsable(gitLabApiV3, gitLabApiV4).getVersion());

        when(gitLabApiV3.getStatus()).thenReturn(new RestApiStatus(true, false, true, true));
        when(gitLabApiV4.getStatus()).thenReturn(new RestApiStatus(true, true, true, true));

        assertEquals(4, locator.findUsable(gitLabApiV3, gitLabApiV4).getVersion());

        when(gitLabApiV3.getStatus()).thenReturn(new RestApiStatus(true, true, true, true));
        when(gitLabApiV4.getStatus()).thenReturn(new RestApiStatus(true, true, true, false));

        assertEquals(3, locator.findUsable(gitLabApiV3, gitLabApiV4).getVersion());
    }
}