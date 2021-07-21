plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")

    id("androidx.navigation.safeargs.kotlin")
    id("com.mikepenz.aboutlibraries.plugin")
    id("com.squareup.sqldelight") version Dependencies.SQL_DELIGHT
    id("com.github.ben-manes.versions") version Dependencies.VERSIONS_PLUGIN

    id("com.diffplug.spotless") version "5.12.5"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1")

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.fragment:fragment-ktx:1.3.5")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    // Required for new java.time.*
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    // Navigation Support
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")

    // For Json
    val moshiVersion = "1.12.0"
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    // For web requests
    val okhttpVersion = "4.9.0"
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")

    // Lifecycle support
    val lifecycleVersion = "2.3.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    // Theming
    implementation("com.google.android.material:material:1.4.0")

    // Custom tabs
    implementation("androidx.browser:browser:1.3.0")

    // For notification service
    val workVersion = "2.5.0"
    implementation("androidx.work:work-runtime-ktx:$workVersion")

    // For logging
    implementation("com.github.ajalt:timberkt:1.5.1")

    // Fast Scroller for recyclerview
    implementation("com.l4digital.fastscroll:fastscroll:2.0.1")

    // For for working with JSoup
    implementation("org.jsoup:jsoup:1.14.1")

    // About page
    implementation("com.mikepenz:aboutlibraries-core:${Dependencies.ABOUT_LIB}")
    implementation("com.mikepenz:aboutlibraries:${Dependencies.ABOUT_LIB}")

    // Database support
    implementation("com.squareup.sqldelight:android-driver:1.5.1")
    implementation("com.squareup.sqldelight:coroutines-extensions-jvm:1.5.1")

    implementation("io.coil-kt:coil:1.3.0")
}

spotless {
    format("misc") {
        target("*.gradle", "*.md", ".gitignore")

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
    java {
        googleJavaFormat("1.8").aosp()
    }
    kotlin {
        target("**/*.kt")
        ktlint("0.41.0").userData(
            mapOf(
                "disabled_rules" to "no-wildcard-imports, no-blank-line-before-rbrace"
            )
        )

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
    kotlinGradle {
        target("**/*.kts")

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
}

tasks.register("spotlessHook") {
    createSpotlessGitHook()
}
