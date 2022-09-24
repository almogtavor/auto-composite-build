package io.github.almogtavor.tasks.internal;

import io.github.almogtavor.utils.AutoCompositeBuildConstants;
import io.github.almogtavor.utils.GitDetailsUtils;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AddRepoToGitDetailsImpl {
    public void addCurrentGitRepoPathToTheDetailsFile(String currentGitRepoPath, Logger logger) {
        File localGitDetailsFile = GitDetailsUtils.getLocalGitDetailsFile();
        try (Stream<String> lines = Files.lines(Paths.get(localGitDetailsFile.getAbsolutePath()))) {
            boolean isCurrentGitRepoPathAlreadyWritten = checkIfCurrentGitRepoPathAlreadyWritten(currentGitRepoPath, lines);
            if (!isCurrentGitRepoPathAlreadyWritten) {
                if (logger !=null) logger.log(LogLevel.QUIET, String.format("Local %s path is %s", AutoCompositeBuildConstants.GIT_DETAILS_FILE_NAME , localGitDetailsFile));
                if (logger !=null) logger.log(LogLevel.QUIET, String.format("The detected local git project path is %s", currentGitRepoPath));
                appendCurrentGitRepoPath(localGitDetailsFile, currentGitRepoPath);
            } else {
                if (logger !=null) logger.log(LogLevel.QUIET, String.format("This repo - %s already exists in Git details.", currentGitRepoPath));
            }
        } catch (IOException e) {
            if (logger !=null) logger.log(LogLevel.ERROR, "An error occurred while appending data into the " + AutoCompositeBuildConstants.GIT_DETAILS_FILE_NAME + " file. " +
                                            "Consider a re-run for this task.");
            e.printStackTrace();
        }
    }

    public void createGitDetailsFileIfNotExists(Logger logger) {
        File localGitDetailsFile = GitDetailsUtils.getLocalGitDetailsFile();
        if (!localGitDetailsFile.exists()) {
            GitDetailsUtils.createFileIfNotExists(localGitDetailsFile, logger, false);
        }
    }

    private void appendCurrentGitRepoPath(File localGitDetailsFile, String currentGitRepoPath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(localGitDetailsFile, true))) {
            bw.append(String.format("%s%s", System.lineSeparator(), currentGitRepoPath));
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
