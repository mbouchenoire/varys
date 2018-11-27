package org.varys.gitlab.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GitLabVersionTest {

    @Test
    public void getMajor() {
        assertEquals(11, new GitLabVersion("11.5.2-rc3").getMajor());
        assertEquals(8, new GitLabVersion("8.3").getMajor());
    }

    @Test
    public void getMinor() {
        assertEquals(5, new GitLabVersion("11.5.2-rc3").getMinor());
        assertEquals(3, new GitLabVersion("8.3").getMinor());
    }
}