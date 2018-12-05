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

import org.eclipse.jgit.lib.Repository;

public class GitRepository {

    private final Repository repository;

    public GitRepository(Repository repository) {
        this.repository = repository;
    }

    private boolean isLocalBranch(String branchName) {
        return branchName.startsWith("refs/heads/");
    }

    public boolean hasLocalBranch(String branchName) {
        return this.repository.getAllRefs().keySet().stream()
                .filter(this::isLocalBranch)
                .map(localBranchName -> localBranchName.replace("refs/heads/", ""))
                .anyMatch(branchName::equals);
    }
}
