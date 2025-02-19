buildscript {
    repositories {
        google()
        mavenCentral()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0") // Update AGP version
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0") // Match Kotlin version
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
}


plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

}