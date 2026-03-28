plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly("com.android.tools.build:gradle:8.5.0")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
    compileOnly("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
    compileOnly("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.0.0-1.0.21")
}
