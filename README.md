# 🏗️ Auto Composite Build Gradle Plugin

The Auto Composite Build Gradle Plugin is designed to solve the path reference issue that arises when using Gradle's composite build feature. Typically, for a team to use a shared common logic project, all developers must clone both the common logic project and the related projects into identical file paths on their local machines. This can cause issues with path references when sharing projects via version control. The Auto Composite Build plugin eliminates this problem by allowing the use of Gradle's composite builds in a Git-compatible way.

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

Auto Composite build has compiled with JDK 1.8 for backward compatibility.

#### Kotlin DSL

In the kotlin-based `settings.gradle.kts`:
```kotlin
plugins {
    id ("io.github.almogtavor.auto-composite-build") version "1.1.0"
}

autoCompositeBuild {
    autoIncludeBuilds("my-first-app", "my-second-app")
}
```

#### Groovy DSL

In the groovy-based `settings.gradle`:
```groovy
plugins {
    id "io.github.almogtavor.auto-composite-build" version "1.1.0"
}

autoCompositeBuild {
    autoIncludeBuilds("my-first-app", "my-second-app")
}
```

## Available Tasks

* `./gradlew addRepoToGitDetails` - Adds the current repository to the local `.auto-composite-build` folder.<br>
    You should run this task for every project you'd like to include as composite build.
    This task will run automatically when configuring the Auto Composite Build plugin.
* `./gradlew includeModulesAsCompositeBuilds` - This task generates the `composite-build.gradle` or `composite-build.gradle.kts`
    that holds the paths for the modules that should be included.<br>
    This task must run only after `addRepoToGitDetails` run in all the modules that you've configured to include.
    This task will run automatically when configuring the Auto Composite Build plugin.
* `./gradlew deleteGitDetails` - Should not be used. This was implemented only for edge cases. 
    This task deletes the `git.details` file (and warns before).

### Limitations

In case of moving the path of the repository of a module that is included as a composite build, the plugin will throw an error.
To get away with this you should rerun the `./gradlew addRepoToGitDetails` task.
In edge cases there's a `./gradlew deleteGitDetails` task which will allow you to delete your local's `git.details` file.

Auto composite build assumes you can add the plugin to the to-be-included project, too.
If you can't, you can go to the `git.details` and add the to-be-included project's path manually.

### Alternatives

There are two relevant alternatives on this subject, that tries to solve a similar problem at the area of Gradle's composite builds.
These are:
- [includegit-gradle-plugin](https://github.com/melix/includegit-gradle-plugin) - 
    which enables to reference a git repository and a local directory to scan.
- [includeme](https://github.com/TradeMe/IncludeMe)
    which will automatically scan the upper directory, of go to further levels if specified.
Both of these won't let you upload the project to Git when some each user of it places it on another path.
Auto composite build tries to solve this by assuming you will already clone the to-be-included project, and that you can add this plugin to it too.
And as said, if not, you can always go to the `git.details` and add its path manually.
