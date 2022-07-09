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

public class ClearGitDetailsTask extends DefaultTask {

    @TaskAction
    public void addProjectToGitDetails() {
        File localGitDetailsFile = GitDetailsUtils.getLocalGitDetailsFile();
        try {
            Files.delete(localGitDetailsFile.toPath());
        } catch (IOException e) {
            getLogger().log(LogLevel.ERROR, "Could not delete " + localGitDetailsFile.toPath() + " file. ");
            e.printStackTrace();
        }
    }
}
