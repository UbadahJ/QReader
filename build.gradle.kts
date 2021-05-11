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
        classpath("com.squareup.sqldelight:gradle-plugin:${Dependencies.SQL_DELIGHT}")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}