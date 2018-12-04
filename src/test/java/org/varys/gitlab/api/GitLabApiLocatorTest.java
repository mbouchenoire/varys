package org.varys.gitlab.api;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GitLabApiLocatorTest {

    @Test
    public void findUsable() {
        final GitLabApiLocator locator = new GitLabApiLocator();
        final GitLabApiV3 gitLabApiV3 = mock(GitLabApiV3.class);
        when(gitLabApiV3.getVersion()).thenReturn(3);
        final GitLabApiV4 gitLabApiV4 = mock(GitLabApiV4.class);
        when(gitLabApiV4.getVersion()).thenReturn(4);

        when(gitLabApiV3.isAuthorized()).thenReturn(false);
        when(gitLabApiV3.isCompatible()).thenReturn(false);

        when(gitLabApiV4.isAuthorized()).thenReturn(false);
        when(gitLabApiV4.isCompatible()).thenReturn(false);

        try {
            locator.findUsable(gitLabApiV3, gitLabApiV4);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("authenticate"));
        }

        when(gitLabApiV3.isAuthorized()).thenReturn(true);
        when(gitLabApiV3.isCompatible()).thenReturn(false);

        when(gitLabApiV4.isAuthorized()).thenReturn(true);
        when(gitLabApiV4.isCompatible()).thenReturn(false);

        try {
            locator.findUsable(gitLabApiV3, gitLabApiV4);
            fail();
        } catch (UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("compatible"));
        }

        when(gitLabApiV3.isAuthorized()).thenReturn(true);
        when(gitLabApiV3.isCompatible()).thenReturn(true);

        when(gitLabApiV4.isAuthorized()).thenReturn(false);
        when(gitLabApiV4.isCompatible()).thenReturn(false);

        assertEquals(3, locator.findUsable(gitLabApiV3, gitLabApiV4).getVersion());

        when(gitLabApiV3.isAuthorized()).thenReturn(true);
        when(gitLabApiV3.isCompatible()).thenReturn(true);

        when(gitLabApiV4.isAuthorized()).thenReturn(true);
        when(gitLabApiV4.isCompatible()).thenReturn(true);

        assertEquals(3, locator.findUsable(gitLabApiV3, gitLabApiV4).getVersion());

        when(gitLabApiV3.isAuthorized()).thenReturn(false);
        when(gitLabApiV3.isCompatible()).thenReturn(false);

        when(gitLabApiV4.isAuthorized()).thenReturn(true);
        when(gitLabApiV4.isCompatible()).thenReturn(true);

        assertEquals(4, locator.findUsable(gitLabApiV3, gitLabApiV4).getVersion());

        when(gitLabApiV3.isAuthorized()).thenReturn(true);
        when(gitLabApiV3.isCompatible()).thenReturn(false);

        when(gitLabApiV4.isAuthorized()).thenReturn(true);
        when(gitLabApiV4.isCompatible()).thenReturn(true);

        assertEquals(4, locator.findUsable(gitLabApiV3, gitLabApiV4).getVersion());

        when(gitLabApiV3.isAuthorized()).thenReturn(true);
        when(gitLabApiV3.isCompatible()).thenReturn(true);

        when(gitLabApiV4.isAuthorized()).thenReturn(false);
        when(gitLabApiV4.isCompatible()).thenReturn(true);

        assertEquals(3, locator.findUsable(gitLabApiV3, gitLabApiV4).getVersion());
    }
}