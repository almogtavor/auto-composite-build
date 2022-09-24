package io.github.almogtavor.tasks;

import io.github.almogtavor.utils.GitDetailsUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ViewGitDetailsTask extends DefaultTask {

    @TaskAction
    public void viewGitDetails() {
        File localGitDetailsFile = GitDetailsUtils.getLocalGitDetailsFile();
        try {
            getLogger().log(LogLevel.LIFECYCLE, String.join("\n", Files.readAllLines(localGitDetailsFile.toPath())));
        } catch (IOException e) {
            getLogger().log(LogLevel.ERROR, "Could not delete " + localGitDetailsFile.toPath() + " file. ");
            e.printStackTrace();
        }
    }
}
