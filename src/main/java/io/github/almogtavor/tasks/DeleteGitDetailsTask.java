package io.github.almogtavor.tasks;

import io.github.almogtavor.utils.GitDetailsUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class DeleteGitDetailsTask extends DefaultTask {

    @TaskAction
    public void deleteGitDetails() {
        File localGitDetailsFile = GitDetailsUtils.getLocalGitDetailsFile();
        try {
            System.out.println("Are you sure you want to delete the Git details file? This will affect all working auto-composite-builds. Respond with (y/n):");
            Scanner scanner = new Scanner(System.in);
            String userDecision = scanner.next();
            if (userDecision.equals("y") || userDecision.equals("Y")) {
                Files.delete(localGitDetailsFile.toPath());
            }
        } catch (IOException e) {
            getLogger().log(LogLevel.ERROR, "Could not delete " + localGitDetailsFile.toPath() + " file. ");
            e.printStackTrace();
        }
    }
}
