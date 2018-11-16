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
