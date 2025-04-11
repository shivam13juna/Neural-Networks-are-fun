// app/build.gradle.kts
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    // Replace kapt with KSP
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
}

android {
    namespace = "com.actiontracker.app" // Changed from example.actiontracker to actiontracker.app
    compileSdk = 33

    defaultConfig {
        applicationId = "com.actiontracker.app" // Changed to unique package name
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("releaseSigning") {
            // Usually replaced by environment variables in CI
            storeFile = file(System.getenv("RELEASE_STORE_FILE") ?: project.property("RELEASE_STORE_FILE").toString())
            storePassword = System.getenv("RELEASE_STORE_PASSWORD") ?: project.property("RELEASE_STORE_PASSWORD").toString()
            keyAlias = System.getenv("RELEASE_KEY_ALIAS") ?: project.property("RELEASE_KEY_ALIAS").toString()
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD") ?: project.property("RELEASE_KEY_PASSWORD").toString()
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("releaseSigning")

            // ProGuard / R8 rules (use default or custom)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Typically no signing config in debug
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    // Experimental if you want ViewBinding or DataBinding
    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    
    // Use JVM toolchain as suggested in the error message
    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }
    
    // KSP configuration for Room
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    packaging {
        resources.excludes.add("META-INF/LICENSE*")
        resources.excludes.add("META-INF/NOTICE*")
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    // Jetpack Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)
    
    // Java 8 Date/Time API backport
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6")

    // Coroutines
    implementation(libs.coroutines.android)

    // Room with KSP instead of kapt
    implementation("androidx.room:room-runtime:2.5.1")
    implementation("androidx.room:room-ktx:2.5.1")
    ksp("androidx.room:room-compiler:2.5.1")

    // Unit Testing
    testImplementation("junit:junit:${libs.versions.junit.get()}")

    // Android Instrumented Testing
    androidTestImplementation("androidx.test.espresso:espresso-core:${libs.versions.espresso.get()}")
    androidTestImplementation("androidx.test:runner:1.5.2")
}
