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
        AutoCompositeBuildExtension extension = project.getExtensions().create("autoCompositeBuild", AutoCompositeBuildExtension.class);
        TaskProvider<GitDetailsTask> addRepoToGitDetailsTask = project
                .getTasks()
                .register("addRepoToGitDetails", GitDetailsTask.class);
        TaskProvider<ClearGitDetailsTask> clearGitDetailsTask = project
                .getTasks()
                .register("deleteGitDetails", ClearGitDetailsTask.class);
        TaskProvider<IncludeModuleAsCompositeBuildTask> compositeBuildTask = project
                .getTasks()
                .register("includeModulesAsCompositeBuilds", IncludeModuleAsCompositeBuildTask.class);
        project.afterEvaluate(p -> compositeBuildTask.configure(t -> t.setAutoCompositeBuildExtension(extension)));
    }
}
