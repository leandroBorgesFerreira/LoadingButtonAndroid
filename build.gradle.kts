// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.sonatype.publish) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

extra["libraryVersion"] = "3.0.0"
