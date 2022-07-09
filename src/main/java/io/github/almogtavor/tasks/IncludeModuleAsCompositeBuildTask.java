package io.github.almogtavor.tasks;

import io.github.almogtavor.AutoCompositeBuildExtension;
import io.github.almogtavor.DslLang;
import io.github.almogtavor.GradleFilesWriter;
import io.github.almogtavor.utils.GitDetailsUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.almogtavor.GradleFilesWriter.DROP_LINE;
import static io.github.almogtavor.utils.AutoCompositeBuildConstants.*;

public class IncludeModuleAsCompositeBuildTask extends DefaultTask {
    private AutoCompositeBuildExtension autoCompositeBuildExtension;

    @Input
    public AutoCompositeBuildExtension getAutoCompositeBuildExtension() {
        return autoCompositeBuildExtension;
    }

    public void setAutoCompositeBuildExtension(AutoCompositeBuildExtension autoCompositeBuildExtension) {
        this.autoCompositeBuildExtension = autoCompositeBuildExtension;
    }

    @TaskAction
    public void includeModulesAsCompositeBuild() {
        Optional.ofNullable(autoCompositeBuildExtension.getModulesNames()).ifPresentOrElse(modules -> {
            File localGitDetailsFile = GitDetailsUtils.getLocalGitDetailsFile();
            List<String> listOfModulesPathsToInclude = new ArrayList<>();
            fillListOfModulesPathsToInclude(modules, listOfModulesPathsToInclude, localGitDetailsFile);
            try {
                addModulesPathsToCompositeBuildGradleFile(listOfModulesPathsToInclude);
            } catch (IOException e) {
                getLogger().log(LogLevel.ERROR, String.format("No module defined. " +
                                                              "Make sure to have a \"$USER_HOME\\%s\\%s\" file configured and re-run the task.",
                        GIT_DETAILS_DIR,
                        GIT_DETAILS_FILE_NAME));
                e.printStackTrace();
            }
            addCompositeBuildGradleFileToGitIgnore();
        }, () -> getLogger().log(LogLevel.ERROR, "No module defined. " +
                                                 "Please configure \"modulesNames\" = \"List.of(your-module-name)\" and re-run the task."));
    }

    private void fillListOfModulesPathsToInclude(List<String> modules, List<String> listOfModulesPathsToInclude, File localGitDetailsFile) {
        for (var module : modules) {
            try (Stream<String> lines = Files.lines(Paths.get(localGitDetailsFile.getAbsolutePath()))) {
                boolean isModuleExists = addModuleToListIfExists(listOfModulesPathsToInclude, module, lines);
                if (!isModuleExists) {
                    getLogger().log(LogLevel.ERROR,
                            String.format("Module does not exists in the \"$USER_HOME\\%s\\%s\" file. " +
                                          "Configure it properly and re-run the task.",
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

    private void addCompositeBuildGradleFileToGitIgnore() {
        String gitIgnorePath = GitDetailsUtils.getCurrentGitRepoPath() + File.separator + ".gitignore";
        File gitIgnoreFile = new File(gitIgnorePath);
        if (gitIgnoreFile.exists()) {
            try {
                String gitIgnoreContent = Files.readString(Path.of(gitIgnorePath));
                if (!gitIgnoreContent.contains(COMPOSITE_BUILD_GRADLE_GROOVY_FILE_NAME)) {
                    Files.write(Paths.get(gitIgnorePath),
                            (DROP_LINE + (autoCompositeBuildExtension.getDslLang() == DslLang.GROOVY ?
                                    COMPOSITE_BUILD_GRADLE_GROOVY_FILE_NAME :
                                    COMPOSITE_BUILD_GRADLE_KOTLIN_FILE_NAME)).getBytes(),
                            StandardOpenOption.APPEND);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean addModuleToListIfExists(List<String> listOfModulesPathsToInclude, String module, Stream<String> lines) {
        for (String line : lines.collect(Collectors.toList())) {
            if (Path.of(line).getFileName().toString().equals(module) && Files.exists(Path.of(line))) {
                listOfModulesPathsToInclude.add(line);
                return true;
            }
        }
        return false;
    }

    private void addModulesPathsToCompositeBuildGradleFile(List<String> listOfModulesPathsToInclude) throws IOException {
        DslLang dslLang = Optional.ofNullable(autoCompositeBuildExtension.getDslLang()).orElse(DslLang.GROOVY);
        File compositeBuildGradleGroovyFile = new File(String.format("%s%s%s", GitDetailsUtils.getCurrentGitRepoPath(),
                File.separator,
                COMPOSITE_BUILD_GRADLE_GROOVY_FILE_NAME));
        File compositeBuildGradleKotlinFile = new File(String.format("%s%s%s", GitDetailsUtils.getCurrentGitRepoPath(),
                File.separator,
                COMPOSITE_BUILD_GRADLE_KOTLIN_FILE_NAME));
        clearFileIfExists(compositeBuildGradleGroovyFile);
        clearFileIfExists(compositeBuildGradleKotlinFile);
        if (dslLang == DslLang.GROOVY) {
            createCompositeBuildFile(compositeBuildGradleGroovyFile, listOfModulesPathsToInclude, dslLang);
        } else {
            createCompositeBuildFile(compositeBuildGradleKotlinFile, listOfModulesPathsToInclude, dslLang);
        }
    }

    private void createCompositeBuildFile(File compositeBuildGradleGroovyFile, List<String> listOfModulesPathsToInclude, DslLang dslLang) throws IOException {
        GitDetailsUtils.createFileIfNotExists(compositeBuildGradleGroovyFile, getLogger(), false);
        new GradleFilesWriter().createCompositeBuildGradleFileFromModulesPaths(listOfModulesPathsToInclude,
                compositeBuildGradleGroovyFile,
                dslLang);
    }

    private void clearFileIfExists(File compositeBuildGradleGroovyFile) throws IOException {
        if (compositeBuildGradleGroovyFile.exists()) Files.delete(compositeBuildGradleGroovyFile.toPath());
    }
}
