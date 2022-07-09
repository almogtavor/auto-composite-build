package io.github.almogtavor.utils;

public class AutoCompositeBuildConstants {
    public static final String GIT_DETAILS_DIR = ".auto-composite-build";
    public static final String GIT_DETAILS_FILE_NAME = "git.details";
    public static final String COMPOSITE_BUILD_GRADLE_GROOVY_FILE_NAME = "composite-build.gradle";
    public static final String COMPOSITE_BUILD_GRADLE_KOTLIN_FILE_NAME = "composite-build.gradle.kts";

    private AutoCompositeBuildConstants() throws IllegalAccessException {
        throw new IllegalAccessException("A utility class");
    }
}
