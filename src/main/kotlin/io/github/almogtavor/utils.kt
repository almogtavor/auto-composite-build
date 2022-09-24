@file:Suppress("unused")

import io.github.almogtavor.AutoCompositeBuildExtension
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.configure

// This function is necessary for introducing an `autoCompositeBuild {}` clause for the `settings.gradle.kts` file.
fun Settings.autoCompositeBuild(repos: AutoCompositeBuildExtension.() -> Unit) = configure(repos)
