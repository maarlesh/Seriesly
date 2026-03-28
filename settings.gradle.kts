pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
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
rootProject.name = "Seriesly"

// App
include(":app")

// Core modules
include(":core:core-common")
include(":core:core-database")
include(":core:core-network")
include(":core:core-domain")
include(":core:core-data")
include(":core:core-ui")
include(":core:core-security")

// Feature modules
include(":feature:feature-auth")
include(":feature:feature-search")
include(":feature:feature-watchlist")
include(":feature:feature-detail")
include(":feature:feature-progress")
include(":feature:feature-profile")
