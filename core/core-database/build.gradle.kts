plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}
android {
    namespace = "com.seriesly.core.database"
    compileSdk = 35
    defaultConfig {
        minSdk = 26
        ksp { arg("room.schemaLocation", "$projectDir/schemas") }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(project(":core:core-common"))
    testImplementation(libs.bundles.testing.unit)
    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.bundles.testing.android)
}
