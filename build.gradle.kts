// build.gradle.kts (Project: YourProjectName)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.services) apply false      // Firebase
    // If you still use Navigation Safe Args (optional)
    id("androidx.navigation.safeargs.kotlin") version "2.8.7" apply false

    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
}

true // required at the end in Kotlin DSL