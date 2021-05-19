import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")

    id("androidx.navigation.safeargs.kotlin")
    id("com.mikepenz.aboutlibraries.plugin")
    id("com.squareup.sqldelight")
    id("com.github.ben-manes.versions") version Dependencies.VERSIONS_PLUGIN
}

android {
    compileSdkVersion(AndroidConfig.compileSdk)
    buildToolsVersion(AndroidConfig.buildTools)

    defaultConfig {
        applicationId = "com.ubadahj.qidianundergroud"
        minSdkVersion(AndroidConfig.minSdk)
        targetSdkVersion(AndroidConfig.targetSdk)
        versionCode = getMasterCommitCount().toInt()
        versionName = getAppVersion()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    buildFeatures {
        viewBinding = true
    }

}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.fragment:fragment-ktx:1.3.3")
    implementation("androidx.recyclerview:recyclerview:1.2.0")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

    // Required for new java.time.*
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    // Navigation Support
    implementation("androidx.navigation:navigation-fragment-ktx:${Dependencies.NAVIGATION}")
    implementation("androidx.navigation:navigation-ui-ktx:${Dependencies.NAVIGATION}")

    // For Json
    val moshi_version = "1.12.0"
    implementation("com.squareup.moshi:moshi:$moshi_version")
    implementation("com.squareup.moshi:moshi-kotlin:$moshi_version")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshi_version")

    // For web requests
    val okhttp_version = "4.9.0"
    val retrofit_version = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit_version")
    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp_version")

    // Lifecycle support
    val lifecycle_version = "2.3.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")

    // Theming
    implementation("com.google.android.material:material:1.3.0")

    // Custom tabs
    implementation("androidx.browser:browser:1.3.0")

    // For notification service
    val work_version = "2.5.0"
    implementation("androidx.work:work-runtime-ktx:$work_version")

    // For logging
    implementation("com.github.ajalt:timberkt:1.5.1")

    // Fast Scroller for recyclerview
    implementation("com.l4digital.fastscroll:fastscroll:2.0.1")

    // For for working with JSoup
    implementation("org.jsoup:jsoup:1.13.1")

    // About page
    implementation("com.mikepenz:aboutlibraries-core:${Dependencies.ABOUT_LIB}")
    implementation("com.mikepenz:aboutlibraries:${Dependencies.ABOUT_LIB}")

    // Database support
    implementation("com.squareup.sqldelight:android-driver:${Dependencies.SQL_DELIGHT}")
    implementation("com.squareup.sqldelight:coroutines-extensions-jvm:${Dependencies.SQL_DELIGHT}")

    implementation("io.coil-kt:coil:1.2.1")
}

fun getAppVersion(): String {
    var version = "1.0b${getMasterCommitCount()}"
    if (getBranchName() != "master") {
        version += "+${runCommand("git rev-list --count HEAD ^master")}"
    }

    return "$version-${getBranchName()}"
}

fun getMasterCommitCount(): String = runCommand("git rev-list --count master")

fun getGitSha(): String = runCommand("git rev-parse --short HEAD")

fun getBranchName(): String = runCommand("git rev-parse --abbrev-ref HEAD")

fun getBuildTime(): String =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date())

fun runCommand(command: String): String {
    val byteOut = ByteArrayOutputStream()
    project.exec {
        commandLine = command.split(" ")
        standardOutput = byteOut
    }
    return String(byteOut.toByteArray()).trim()
}