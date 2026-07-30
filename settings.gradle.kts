pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MSport"
include(":app")
include(":core:model")
include(":core:network")
include(":core:common")
include(":core:domain")
include(":core:mvi")
include(":core:data")
include(":core:database")
include(":core:designsystem")
include(":feature:auth")
include(":feature:workout")
include(":feature:profile")
