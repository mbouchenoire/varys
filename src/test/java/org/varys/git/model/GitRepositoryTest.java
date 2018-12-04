package org.varys.git.model;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class GitRepositoryTest {

    @Test
    public void hasLocalBranch() {
        final Repository repository = Mockito.mock(Repository.class);
        final Map<String, Ref> refs = new HashMap<>();
        refs.put("refs/heads/master", null);
        refs.put("refs/heads/maxime", null);
        refs.put("refs/remotes/master", null);
        refs.put("refs/remotes/kevin", null);
        when(repository.getAllRefs()).thenReturn(refs);

        final GitRepository gitRepository = new GitRepository(repository);
        assertTrue(gitRepository.hasLocalBranch("master"));
        assertTrue(gitRepository.hasLocalBranch("maxime"));
        assertFalse(gitRepository.hasLocalBranch("kevin"));
    }
}