package io.github.almogtavor;

import io.github.almogtavor.tasks.internal.AddRepoToGitDetailsImpl;
import io.github.almogtavor.utils.GitDetailsUtils;
import org.gradle.api.initialization.Settings;
import org.gradle.api.logging.LogLevel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.almogtavor.utils.AutoCompositeBuildConstants.GIT_DETAILS_DIR;
import static io.github.almogtavor.utils.AutoCompositeBuildConstants.GIT_DETAILS_FILE_NAME;
import static io.github.almogtavor.utils.GitDetailsUtils.getLocalGitDetailsFile;

public class AutoCompositeBuildExtension {
    private List<String> modulesNames;
    private final Settings settings;

    public AutoCompositeBuildExtension(List<String> modulesNames, Settings settings) {
        this.modulesNames = modulesNames;
        this.settings = settings;
    }

    public void autoIncludeBuilds(String... reposDirsNames) {
        addRepoToGitDetails();
        this.modulesNames = Arrays.stream(reposDirsNames).collect(Collectors.toList());
        for (String repoDirName: reposDirsNames) {
            try (Stream<String> localReposPaths = Files.lines(Paths.get(getLocalGitDetailsFile().getAbsolutePath()))) {
                Optional<String> localRepoPath = GitDetailsUtils.getModulePath(repoDirName, localReposPaths.collect(Collectors.toList()));
                if (localRepoPath.isPresent()) {
                    settings.includeBuild(localRepoPath.get());
                }
            } catch (IOException e) {
                settings.getGradle().getRootProject().getLogger().log(LogLevel.ERROR, String.format("An error occurred when tried to access $USER_HOME " + "Make sure to have a \"$USER_HOME\\%s\\%s\" file configured and re-run the task.",
                        GIT_DETAILS_DIR,
                        GIT_DETAILS_FILE_NAME));
                e.printStackTrace();
            }
        }
    }

    private void addRepoToGitDetails() {
        AddRepoToGitDetailsImpl addRepoToGitDetails = new AddRepoToGitDetailsImpl();
        addRepoToGitDetails.createGitDetailsFileIfNotExists(null);
        addRepoToGitDetails.addCurrentGitRepoPathToTheDetailsFile(GitDetailsUtils.getCurrentGitRepoPath(), null);
    }

    public Settings getSettings() {
        return settings;
    }

    public List<String> getModulesNames() {
        return modulesNames;
    }

    public void setModulesNames(List<String> modulesNames) {
        this.modulesNames = modulesNames;
    }
}