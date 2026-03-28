import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("seriesly.android.library")
            pluginManager.apply("seriesly.android.hilt")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            dependencies {
                add("implementation", project(":core:core-ui"))
                add("implementation", project(":core:core-domain"))
                add("implementation", project(":core:core-common"))
                add("implementation", project(":core:core-data"))
            }
        }
    }
}
