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

package org.varys.git.service;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.varys.common.model.GitConfig;
import org.varys.common.service.Log;
import org.varys.git.model.GitRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitService {

    private static final int MAX_DEPTH = 3;

    private final GitConfig config;

    public GitService(GitConfig config) {
        this.config = config;
    }

    private static Optional<Repository> toRepository(Path gitDirectoryPath) {
        try {
            final Repository repository = new FileRepositoryBuilder()
                    .setGitDir(gitDirectoryPath.toFile())
                    .build();

            return Optional.ofNullable(repository);
        } catch (IOException e) {
            Log.error(e, "Failed to parse directory as Git repository: {}", gitDirectoryPath);
            return Optional.empty();
        }
    }

    private Collection<GitRepository> findLocalRepositories() {
        final File rootDirectory = this.config.getParentDirectory();

        final BiPredicate<Path, BasicFileAttributes> isGitDirectory =
                (filePath, fileAttr) -> filePath.endsWith(".git");

        try (Stream<Path> pathStream = Files.find(rootDirectory.toPath(), MAX_DEPTH, isGitDirectory)) {
            return pathStream
                    .map(GitService::toRepository)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(GitRepository::new)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            Log.error(e, "Failed to list local Git repositories");
            return Collections.emptyList();
        }
    }

    public boolean hasLocalBranch(String branchName) {
        return this.findLocalRepositories().stream()
                .anyMatch(gitRepository -> gitRepository.hasLocalBranch(branchName));
    }
}
