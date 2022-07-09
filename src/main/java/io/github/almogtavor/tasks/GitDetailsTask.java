package io.github.almogtavor.tasks;

import io.github.almogtavor.utils.AutoCompositeBuildConstants;
import io.github.almogtavor.utils.GitDetailsUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitDetailsTask extends DefaultTask {

    @TaskAction
    public void addProjectToGitDetails() {
        File localGitDetailsFile = GitDetailsUtils.getLocalGitDetailsFile();
        String currentGitRepoPath = GitDetailsUtils.getCurrentGitRepoPath();
        createFileIfNotExists(localGitDetailsFile);
        addCurrentGitRepoPathToTheDetailsFile(localGitDetailsFile, currentGitRepoPath);
        getLogger().log(LogLevel.QUIET, String.format("Local %s path is %s", AutoCompositeBuildConstants.GIT_DETAILS_FILE_NAME , localGitDetailsFile));
        getLogger().log(LogLevel.QUIET, String.format("The detected local git project path is %s", currentGitRepoPath));
    }

    private void addCurrentGitRepoPathToTheDetailsFile(File localGitDetailsFile, String currentGitRepoPath) {
        try (Stream<String> lines = Files.lines(Paths.get(localGitDetailsFile.getAbsolutePath()))) {
            boolean isCurrentGitRepoPathAlreadyWritten = checkIfCurrentGitRepoPathAlreadyWritten(currentGitRepoPath, lines);
            if (!isCurrentGitRepoPathAlreadyWritten) {
                appendCurrentGitRepoPath(localGitDetailsFile, currentGitRepoPath);
            }
        } catch (IOException e) {
            getLogger().log(LogLevel.ERROR, "An error occurred while appending data into the " + AutoCompositeBuildConstants.GIT_DETAILS_FILE_NAME + " file. " +
                                            "Consider a re-run for this task.");
            e.printStackTrace();
        }
    }

    private void createFileIfNotExists(File localGitDetailsFile) {
        if (!localGitDetailsFile.exists()) {
            GitDetailsUtils.createFileIfNotExists(localGitDetailsFile, getLogger(), false);
        }
    }

    private void appendCurrentGitRepoPath(File localGitDetailsFile, String currentGitRepoPath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(localGitDetailsFile, true))) {
            bw.append(String.format("%s%s", currentGitRepoPath, System.lineSeparator()));
        }
    }

    private boolean checkIfCurrentGitRepoPathAlreadyWritten(String currentGitRepoPath, Stream<String> lines) {
        boolean currentGitRepoPathExists = false;
        for (String line : lines.collect(Collectors.toList())) {
            if (line.equals(currentGitRepoPath)) {
                currentGitRepoPathExists = true;
                break;
            }
        }
        return currentGitRepoPathExists;
    }
}
