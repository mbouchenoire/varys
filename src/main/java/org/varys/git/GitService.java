package org.varys.git;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.varys.common.model.GitConfig;
import org.varys.common.service.Log;
import org.varys.git.model.GitRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        final String rootDirectoryPath = this.config.getParentDirectory();

        final BiPredicate<Path, BasicFileAttributes> isGitDirectory =
                (filePath, fileAttr) -> filePath.endsWith(".git");

        try (Stream<Path> pathStream = Files.find(Paths.get(rootDirectoryPath), MAX_DEPTH, isGitDirectory)) {
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
