# 🏗️ Auto Composite Build Gradle Plugin

A Gradle plugin that can be used to prevent the paths references problem of the Gradle's composite build feature.
Currently, composite builds works in such way that if a team uses this for a common logic project,
all the participants' computers need to clone the common logic project & the projects that use it in the exact same way.

### A demonstration for the problem of Gradle's composite build:
Let's assume one computer #1 cloned project `common-logic` to `C:\\code\common-logic` and uses it in `C:\\code\service1`.
```
computer #1
└── C
    └── code
        ├── common-logic
        └── service1
```
So `C:\\code\service1` will have the line of `include("../common-logic")` 
or `include("C:\\code\common-logic")` on `settings.gradle`.
<br>Another had the following hierarchy.
```
computer #2
└── C
    ├── my-code
    │   └── common-logic
    └── service1
```
So `C:\\service1` will need to have the line of `include("../my-code/common-logic")` 
or `include("C:\\my-code\common-logic")` on `settings.gradle`.
<br>In both declarations, if the `settings.gradle` file will get uploaded to Git, the Gradle's composite build feature will break.

## How Auto Composite Build solved this?
The Auto Composite Build plugin registers `common-logic` to a global file inside the user's home. 
Projects can query the module from the file, and it doesn't matter the structure of the projects on the developer's computer.
```
computer #1
└── C
    ├── my-code
    │   └── common-logic
    ├── service1
    └── Users
        └── OurExampleUser
            └── .auto-composite-build
                └── git.details
```

## Usage

#### Kotlin DSL

`build.gradle`
```kotlin
plugins {
    id ("io.github.almogtavor.auto-composite-build") version "1.0.2"
}

autoCompositeBuild {
    modulesNames = listOf("my-first-app", "my-second-app")
    dslLang = io.github.almogtavor.DslLang.KOTLIN
}
```

In the kotlin-based `settings.gradle.kts`:
```kotlin
import java.io.File

var compositeBuildFileName = "composite-build.gradle.kts"
if (file(compositeBuildFileName).exists()) {
    apply {
        from(compositeBuildFileName)
    }
    if (extra.has("modulesPaths")) {
        @Suppress("UNCHECKED_CAST")
        for (modulePath: String in (extra["modulesPaths"] as List<String>)) {
            if (File(modulePath).exists()) {
                includeBuild(modulePath)
            }
        }
    }
}
```

#### Groovy DSL

`build.gradle`
```groovy
plugins {
    id "io.github.almogtavor.auto-composite-build" version "1.0.2"
}

autoCompositeBuild {
    modulesNames = List.of('my-first-app', 'my-second-app')
    dslLang = "groovy"
}
```

In the groovy-based `settings.gradle`:
```groovy
def compositeBuildFileName = 'composite-build.gradle'
if (file(compositeBuildFileName).exists()) {
    apply from: compositeBuildFileName
    if (modulesPaths != null) {
        for (modulePath in modulesPaths) {
            if (new File(modulePath.toString()).exists()) {
                includeBuild(modulePath)
            }
        }
    }
}
```

## Available Tasks

* `./gradlew addRepoToGitDetails` - Adds the current repository to the local `.gradle-auto-composite-build` folder.<br>
    **You should run this task for every project you'd like to include as composite build.**
* `./gradlew includeModulesAsCompositeBuilds` - This task generates the `composite-build.gradle` or `composite-build.gradle.kts`
    that holds the paths for the modules that should be included.<br>
    This task must run only after you've run `addRepoToGitDetails` in all the modules that you've configured to include.
* `./gradlew deleteGitDetails` - Should not be used. This was implemented only for edge cases. 
    This task deletes the `git-details` file.

### Limitations

In case of moving the path of the repository of a module that is included as a composite build, the plugin will throw an error.
To get away with this you should rerun the `./gradlew addRepoToGitDetails` task.
In edge cases there's `./gradlew deleteGitDetails` which will allow you to delete your local's `git.details` file.
