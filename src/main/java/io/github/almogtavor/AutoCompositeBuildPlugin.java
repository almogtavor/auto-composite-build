package io.github.almogtavor;

import io.github.almogtavor.tasks.DeleteGitDetailsTask;
import io.github.almogtavor.tasks.AddRepoToGitDetailsTask;
import io.github.almogtavor.tasks.IncludeModuleAsCompositeBuildTask;
import io.github.almogtavor.tasks.ViewGitDetailsTask;
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
                .register("viewGitDetails", ViewGitDetailsTask.class, task -> task.setGroup(autoCompositeBuildGroupName));
        project
                .getTasks()
                .register("addRepoToGitDetails", AddRepoToGitDetailsTask.class, task -> task.setGroup(autoCompositeBuildGroupName));
        TaskProvider<IncludeModuleAsCompositeBuildTask> compositeBuildTask = project
                .getTasks()
                .register("includeModulesAsCompositeBuilds",
                        IncludeModuleAsCompositeBuildTask.class,
                        task -> task.setGroup(autoCompositeBuildGroupName));
        project
                .getTasks()
                .register("deleteGitDetails", DeleteGitDetailsTask.class, task -> task.setGroup(autoCompositeBuildGroupName));
        project.afterEvaluate(p -> compositeBuildTask.configure(t -> t.setAutoCompositeBuildExtension(extension)));
    }
}
