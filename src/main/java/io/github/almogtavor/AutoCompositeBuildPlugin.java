package io.github.almogtavor;

import io.github.almogtavor.tasks.ClearGitDetailsTask;
import io.github.almogtavor.tasks.GitDetailsTask;
import io.github.almogtavor.tasks.IncludeModuleAsCompositeBuildTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public class AutoCompositeBuildPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        String autoCompositeBuildGroupName = "auto composite build";
        String autoCompositeBuildExtensionName = "autoCompositeBuild";
        AutoCompositeBuildExtension extension = project.getExtensions().create(autoCompositeBuildExtensionName, AutoCompositeBuildExtension.class);
        project
                .getTasks()
                .register("addRepoToGitDetails", GitDetailsTask.class, task -> task.setGroup(autoCompositeBuildGroupName));
        project
                .getTasks()
                .register("deleteGitDetails", ClearGitDetailsTask.class, task -> task.setGroup(autoCompositeBuildGroupName));
        TaskProvider<IncludeModuleAsCompositeBuildTask> compositeBuildTask = project
                .getTasks()
                .register("includeModulesAsCompositeBuilds",
                        IncludeModuleAsCompositeBuildTask.class,
                        task -> task.setGroup(autoCompositeBuildGroupName));
        project.afterEvaluate(p -> compositeBuildTask.configure(t -> t.setAutoCompositeBuildExtension(extension)));
    }
}
