// app/build.gradle.kts
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    // Replace kapt with KSP
    id("com.google.devtools.ksp") version "2.0.0-1.0.21"
}

android {
    namespace = "com.actiontracker.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.actiontracker.app" // Changed to unique package name
        minSdk = 21
        targetSdk = 35 // Updated targetSdk to 35
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
        // dataBinding disabled to avoid binding conflicts; using viewBinding only
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(17)
        targetCompatibility = JavaVersion.toVersion(17)
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    
    // Use JVM toolchain as suggested in the error message
    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
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
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.9")

    // Coroutines
    implementation(libs.coroutines.android)

    // Room with KSP instead of kapt
    implementation("androidx.room:room-runtime:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")
    ksp("androidx.room:room-compiler:2.7.0")

    // Unit Testing
    testImplementation("junit:junit:4.13.2")

    // Android Instrumented Testing
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    
    // Color Picker Wheel
    implementation("com.github.skydoves:colorpickerview:2.3.0")

    // MPAndroidChart for plotting graphs
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")
}
