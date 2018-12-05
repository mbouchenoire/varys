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