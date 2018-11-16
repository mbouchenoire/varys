package org.varys.git;

import org.varys.common.model.GitConfig;
import org.varys.git.model.GitRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class GitService {

    private static final int MAX_DEPTH = 3;

    private final GitConfig config;

    public GitService(GitConfig config) {
        this.config = config;
    }

    private static Repository toRepository(Path gitDirectoryPath) {
        try {
            return new FileRepositoryBuilder()
                    .setGitDir(gitDirectoryPath.toFile())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to process git directory", e);
        }
    }

    private Collection<GitRepository> findLocalRepositories() {
        final String rootDirectoryPath = this.config.getParentDirectory();

        final BiPredicate<Path, BasicFileAttributes> isGitDirectory =
                (filePath, fileAttr) -> filePath.endsWith(".git");

        try {

            return Files
                    .find(Paths.get(rootDirectoryPath), MAX_DEPTH, isGitDirectory)
                    .map(GitService::toRepository)
                    .map(GitRepository::new)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasLocalBranch(String branchName) {
        return this.findLocalRepositories().stream()
                .anyMatch(gitRepository -> gitRepository.hasLocalBranch(branchName));
    }
}
