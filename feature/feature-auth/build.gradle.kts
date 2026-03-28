plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}
android {
    namespace = "com.seriesly.feature.auth"
    compileSdk = 35
    defaultConfig { minSdk = 26 }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.bundles.lifecycle)
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-common"))
    implementation(project(":core:core-data"))
    implementation(project(":core:core-database"))
    implementation(project(":core:core-security"))
    implementation(libs.room.ktx)
    testImplementation(libs.bundles.testing.unit)
    androidTestImplementation(libs.bundles.testing.android)
}
