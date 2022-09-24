package io.github.almogtavor.tasks;

import io.github.almogtavor.AutoCompositeBuildExtension;
import io.github.almogtavor.utils.GitDetailsUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.initialization.Settings;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.almogtavor.utils.AutoCompositeBuildConstants.GIT_DETAILS_DIR;
import static io.github.almogtavor.utils.AutoCompositeBuildConstants.GIT_DETAILS_FILE_NAME;
import static io.github.almogtavor.utils.GitDetailsUtils.getLocalGitDetailsFile;

public class IncludeModuleAsCompositeBuildTask extends DefaultTask {
    private AutoCompositeBuildExtension autoCompositeBuildExtension;
    private Settings settings;

    @Input
    public AutoCompositeBuildExtension getAutoCompositeBuildExtension() {
        return autoCompositeBuildExtension;
    }

    public void setAutoCompositeBuildExtension(AutoCompositeBuildExtension autoCompositeBuildExtension) {
        this.autoCompositeBuildExtension = autoCompositeBuildExtension;
    }

    @Input
    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    @TaskAction
    public void includeModulesAsCompositeBuild() {
        if (Optional.ofNullable(autoCompositeBuildExtension.getModulesNames()).isPresent()) {
            List<String> modules = autoCompositeBuildExtension.getModulesNames();
            List<String> listOfModulesPathsToInclude = new ArrayList<>();
            fillListOfModulesPathsToInclude(modules, listOfModulesPathsToInclude);
            for (String modulePath : listOfModulesPathsToInclude) {
                settings.includeBuild(modulePath);
            }
        } else {
            getLogger().log(LogLevel.WARN, "No module defined. " +
                                            "If that's not intentional, please configure autoIncludeBuilds(\"my-first-app\", \"my-second-app\"). " +
                                           "Or alternatively, configure \"modulesNames\" = \"List.of(your-module-name)\" and re-run the task.");
        }
    }

    private void fillListOfModulesPathsToInclude(List<String> modules, List<String> listOfModulesPathsToInclude) {
        for (String module : modules) {
            try (Stream<String> localReposPaths = Files.lines(Paths.get(getLocalGitDetailsFile().getAbsolutePath()))) {
                Optional<String> localRepoPath = GitDetailsUtils.getModulePath(module, localReposPaths.collect(Collectors.toList()));
                if (localRepoPath.isPresent()) {
                    listOfModulesPathsToInclude.add(localRepoPath.get());
                } else {
                    getLogger().log(LogLevel.ERROR,
                            String.format("Module %s does not exists in the \"$USER_HOME\\%s\\%s\" file. " +
                                          "Configure it properly and re-run the task.",
                                    module,
                                    GIT_DETAILS_DIR,
                                    GIT_DETAILS_FILE_NAME));
                }
            } catch (IOException e) {
                getLogger().log(LogLevel.ERROR, String.format("An error occurred when tried to access $USER_HOME " +
                                                              "Make sure to have a \"$USER_HOME\\%s\\%s\" file configured and re-run the task.",
                        GIT_DETAILS_DIR,
                        GIT_DETAILS_FILE_NAME));
                e.printStackTrace();
            }
        }
    }
}
