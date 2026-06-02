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

rootProject.name = "Done"

include(":app")
include(":core:core-database")
include(":core:core-domain")
include(":core:core-ui")
include(":core:core-testing")
include(":feature:feature-today")
include(":feature:feature-detail")
include(":feature:feature-manage")
include(":feature:feature-stats")
