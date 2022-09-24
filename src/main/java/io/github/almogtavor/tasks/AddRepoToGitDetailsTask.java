package io.github.almogtavor.tasks;

import io.github.almogtavor.tasks.internal.AddRepoToGitDetailsImpl;
import io.github.almogtavor.utils.GitDetailsUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class AddRepoToGitDetailsTask extends DefaultTask {
    @TaskAction
    public void addProjectToGitDetails() {
        AddRepoToGitDetailsImpl addRepoToGitDetails = new AddRepoToGitDetailsImpl();
        addRepoToGitDetails.createGitDetailsFileIfNotExists(getLogger());
        addRepoToGitDetails.addCurrentGitRepoPathToTheDetailsFile(GitDetailsUtils.getCurrentGitRepoPath(), getLogger());
    }
}
