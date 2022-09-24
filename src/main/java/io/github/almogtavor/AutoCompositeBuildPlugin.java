package io.github.almogtavor;

import io.github.almogtavor.tasks.AddRepoToGitDetailsTask;
import io.github.almogtavor.tasks.DeleteGitDetailsTask;
import io.github.almogtavor.tasks.IncludeModuleAsCompositeBuildTask;
import io.github.almogtavor.tasks.ViewGitDetailsTask;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;


public class AutoCompositeBuildPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings settings) {
        String autoCompositeBuildGroupName = "auto composite build";
        String autoCompositeBuildExtensionName = "autoCompositeBuild";
        AutoCompositeBuildExtension extension = settings
                .getExtensions()
                .create(autoCompositeBuildExtensionName,
                        AutoCompositeBuildExtension.class,
                        settings);
        settings.getGradle().projectsEvaluated(gradle -> {
            gradle.getRootProject()
                    .getTasks()
                    .register("viewGitDetails", ViewGitDetailsTask.class,
                            task -> task.setGroup(autoCompositeBuildGroupName));
            gradle.getRootProject()
                    .getTasks()
                    .register("addRepoToGitDetails", AddRepoToGitDetailsTask.class,
                            task -> task.setGroup(autoCompositeBuildGroupName));
            gradle.getRootProject()
                    .getTasks()
                    .register("deleteGitDetails", DeleteGitDetailsTask.class, task ->
                            task.setGroup(autoCompositeBuildGroupName));

            gradle.getRootProject().getTasks()
                    .register("includeModulesAsCompositeBuilds",
                            IncludeModuleAsCompositeBuildTask.class,
                            task -> task.setGroup(autoCompositeBuildGroupName))
                    .configure(includeModuleTask -> {
                        includeModuleTask.setAutoCompositeBuildExtension(extension);
                        includeModuleTask.setSettings(settings);
                    });
        });
    }
}
