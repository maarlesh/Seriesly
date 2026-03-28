plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}
android {
    namespace = "com.seriesly.core.network"
    compileSdk = 35
    defaultConfig { minSdk = 26 }
    buildFeatures { buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation(libs.bundles.network)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.timber)
    implementation(project(":core:core-common"))
    implementation(project(":core:core-database"))
    testImplementation(libs.bundles.testing.unit)
}
