allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
}

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Dependencies.ANDROID_GRADLE}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Dependencies.KOTLIN}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Dependencies.NAVIGATION}")
        classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:${Dependencies.ABOUT_LIB}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Dependencies.HILT}")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.7.1")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}