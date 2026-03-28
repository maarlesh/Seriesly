plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}
android {
    namespace = "com.seriesly.core.security"
    compileSdk = 35
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation(libs.security.crypto)
    implementation(libs.jbcrypt)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(project(":core:core-common"))
    testImplementation(libs.bundles.testing.unit)
}
