import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics.plugin)
}

val localProperties = Properties().also { props ->
    rootProject.file("local.properties").takeIf { it.exists() }
        ?.inputStream()?.use { props.load(it) }
}

android {
    namespace = "com.seriesly"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.maarlesh.seriesly"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "1.0.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "TVDB_API_KEY", "\"\"")
    }

    signingConfigs {
        create("release") {
            storeFile = file(localProperties["keystore.storeFile"] as? String ?: "keystore/seriesly-release-key")
            storePassword = localProperties["keystore.storePassword"] as? String ?: ""
            keyAlias = localProperties["keystore.keyAlias"] as? String ?: ""
            keyPassword = localProperties["keystore.keyPassword"] as? String ?: ""
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
            buildConfigField(
                "String",
                "TVDB_API_KEY",
                "\"${localProperties["tvdb_api_key"] ?: System.getenv("TVDB_API_KEY") ?: ""}\""
            )
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    implementation(project(":core:core-common"))
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-data"))
    implementation(project(":core:core-database"))
    implementation(project(":core:core-network"))
    implementation(project(":core:core-security"))
    implementation(project(":feature:feature-auth"))
    implementation(project(":feature:feature-search"))
    implementation(project(":feature:feature-watchlist"))
    implementation(project(":feature:feature-detail"))
    implementation(project(":feature:feature-progress"))
    implementation(project(":feature:feature-profile"))

    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    implementation(libs.work.runtime.ktx)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config)

    implementation(libs.coil.compose)

    implementation(libs.timber)

    testImplementation(libs.bundles.testing.unit)
    androidTestImplementation(libs.bundles.testing.android)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}
