package io.github.almogtavor.utils;

public class AutoCompositeBuildConstants {
    public static final String GIT_DETAILS_DIR = ".auto-composite-build";
    public static final String GIT_DETAILS_FILE_NAME = "git.details";

    private AutoCompositeBuildConstants() throws IllegalAccessException {
        throw new IllegalAccessException("A utility class");
    }
}
