package io.github.almogtavor.utils;

import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static io.github.almogtavor.utils.AutoCompositeBuildConstants.GIT_DETAILS_DIR;
import static io.github.almogtavor.utils.AutoCompositeBuildConstants.GIT_DETAILS_FILE_NAME;

public class GitDetailsUtils {

    private GitDetailsUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static File getLocalGitDetailsFolder(String... properties) {
        StringBuilder filePath = new StringBuilder();
        for (var property : properties) {
            filePath.append(property).append(File.separatorChar);
        }
        return new File(filePath.toString());
    }

    public static File getLocalGitDetailsFolder() {
        return getLocalGitDetailsFolder(System.getProperty("user.home"), GIT_DETAILS_DIR);
    }

    public static File getLocalGitDetailsFile() {
        return getLocalGitDetailsFolder(getLocalGitDetailsFolder().getAbsolutePath(), GIT_DETAILS_FILE_NAME);
    }

    public static String getCurrentGitRepoPath() {
        return System.getProperty("user.dir");
    }

    public static void createFileIfNotExists(File localGitDetailsFile, Logger logger, boolean clearFileIfAlreadyExists) {
        try {
            if (!localGitDetailsFile.exists() && localGitDetailsFile.getParentFile().mkdirs()) {
                logger.log(LogLevel.LIFECYCLE, String.format("Successfully created the %s directory.", localGitDetailsFile.getParentFile()));
                Files.createFile(localGitDetailsFile.toPath());
                logger.log(LogLevel.LIFECYCLE, String.format("Successfully created the %s file.", localGitDetailsFile));
            } else if (localGitDetailsFile.exists() && clearFileIfAlreadyExists) {
                Files.delete(localGitDetailsFile.toPath());
                logger.log(LogLevel.DEBUG, "Successfully cleared \"composite-build.gradle\".");
                Files.createFile(localGitDetailsFile.toPath());
                logger.log(LogLevel.LIFECYCLE, String.format("Successfully created the %s file.", localGitDetailsFile));
            }
        } catch (IOException e) {
            logger.log(LogLevel.ERROR, String.format("An error occurred in the creation of the %s file or folder. " +
                                                     "Consider a re-run for this task.", localGitDetailsFile.toPath()));
            e.printStackTrace();
        }
    }
}
