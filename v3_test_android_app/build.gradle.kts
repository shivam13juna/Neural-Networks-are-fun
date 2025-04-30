// build.gradle.kts (Root Project)
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("com.google.devtools.ksp") version "2.0.0-1.0.21"
}

android {
    namespace = "com.actiontracker.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.actiontracker.app"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("releaseSigning") {
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

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(17)
        targetCompatibility = JavaVersion.toVersion(17)
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
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

    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)

    implementation("com.jakewharton.threetenabp:threetenabp:1.4.9")
    implementation(libs.coroutines.android)

    implementation("androidx.room:room-runtime:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")
    ksp("androidx.room:room-compiler:2.7.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")

    implementation("com.github.skydoves:colorpickerview:2.3.0")
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")
}
