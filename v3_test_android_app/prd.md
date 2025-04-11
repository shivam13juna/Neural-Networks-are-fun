Below is a **single, comprehensive `prd.md`** file that you can copy-paste into your agent’s interface. It’s carefully structured to give a junior developer (or an automated build agent) *everything* needed to build, test, sign, and publish the “Action Tracking App” as an **APK** or **AAB** on the Play Store. This document includes:

- Clear functional requirements and design specs for the app.  
- Detailed instructions for project structure, MVVM architecture, and data persistence.  
- A ready-to-copy Gradle configuration (root, settings, module build scripts, version catalogs, properties) that should run “out of the box” with only a few required modifications (like changing application IDs and keystore information).

> **IMPORTANT**  
> 1. You must rename the application ID from `com.example.actiontracker` to a unique ID.  
> 2. You must configure your own keystore and sign the app with unique credentials.  
> 3. You must handle secrets (like keystore passwords) securely (e.g., environment variables, GitHub Actions secrets).  
> 4. Make sure you have installed the required JDK, Android Studio + SDK, and Gradle versions as specified.

---

# PRD: Action Tracking App (Production-Ready)

## Table of Contents
1. [Project Overview](#1-project-overview)  
2. [Features & User Requirements](#2-features--user-requirements)  
3. [Technical Requirements & Assumptions](#3-technical-requirements--assumptions)  
4. [Architecture & Design](#4-architecture--design)  
5. [Data Persistence](#5-data-persistence)  
6. [App UI & UX Details](#6-app-ui--ux-details)  
7. [Project Structure](#7-project-structure)  
8. [Build Configuration (Gradle)](#8-build-configuration-gradle)  
   - [8.1 The Gradle Wrapper](#81-the-gradle-wrapper)  
   - [8.2 `settings.gradle.kts` Example](#82-settingsgradlekts-example)  
   - [8.3 `libs.versions.toml` (Version Catalog)](#83-libsversionstoml-version-catalog)  
   - [8.4 Root `build.gradle.kts` Example](#84-root-buildgradlekts-example)  
   - [8.5 `gradle.properties` Example](#85-gradleproperties-example)  
   - [8.6 App Module `build.gradle.kts` Example](#86-app-module-buildgradlekts-example)  
   - [8.7 ProGuard / R8 Rules (Consumer Rules)](#87-proguard--r8-rules-consumer-rules)  
9. [Testing & Quality Assurance](#9-testing--quality-assurance)  
10. [Release & Publishing Workflow](#10-release--publishing-workflow)  
11. [Production Checklist](#11-production-checklist)  
12. [Future Enhancements](#12-future-enhancements)  

---

## 1. Project Overview
This Android application allows users to track custom, user-defined actions (e.g., “drinking water”) across days. Each action has a **global** counter that appears on every calendar day with a default value of zero. Users can increment/decrement these counters by tapping UI buttons, even on past days.

### Core Goals
1. **Action Management:** Let users create unlimited custom actions.  
2. **Global Visibility:** Newly added actions appear on every day (past and future).  
3. **Daily Tracking & Navigation:** 
   - Users see the current day by default (with all actions and counters).  
   - Users can navigate through dates (previous days, future days) to view/modify counters.  
4. **Zero-Constraints:** Counters cannot drop below zero.  

---

## 2. Features & User Requirements

1. **Add Action:**
   - User can type a name (“Water”, “Push-ups”).
   - The newly created action is instantly visible on **all days** with an initial counter of 0.

2. **Increment/Decrement:**
   - Each action row has a “-” button on the left, a display of the current count + the action name, and a “+” button on the right.
   - Tapping “+” increments the count by 1.
   - Tapping “-” decrements the count by 1 (and never goes below zero).

3. **Day Navigation:**
   - Default view is the current day.
   - A date-switcher (either a top bar with arrows or a calendar UI) allows navigating to previous/future days.

4. **Data Persistence:**
   - The total count for each action is stored and retained locally. Changes are not lost when the app restarts.

5. **Accessibility:**
   - All buttons have content descriptions for screen readers.
   - High contrast text and scalable fonts.

6. **Edge Cases & Error Handling:**
   - Duplicate action names: either allow them with a warning or normalize them (e.g., “Water” vs. “water”).
   - No negative counters.
   - Large numbers of actions (performance considerations if user adds 100+ actions).

---

## 3. Technical Requirements & Assumptions

1. **Minimum SDK**: 21 (Android 5.0).  
2. **Target SDK**: 33 or 34 (latest stable).  
3. **Language**: Kotlin (1.8+) recommended.  
4. **Architecture**: MVVM with ViewModels, LiveData/Flow, or Jetpack Compose (optional).  
5. **Persistence**: Room or SQLite (Room recommended).  
6. **Build Tools**:  
   - Android Gradle Plugin (AGP) ~8.0 or newer.  
   - Gradle 8.2 or newer.  
7. **Testing**: JUnit4/JUnit5, Espresso, possible Compose test if using Compose.  
8. **UI Library**: AndroidX, Material Components.  
9. **State Management**: ViewModel + LiveData/StateFlow.  

> **Assumption**: All data is stored locally; no cloud sync in this initial release.

---

## 4. Architecture & Design

### 4.1 MVVM Approach
- **Model**: Data classes `Action`, `DayRecord` plus Room database entities.  
- **View**: Activities/Fragments displaying counters, date pickers, etc.  
- **ViewModel**: Holds day-based data, calls repository methods to update counters.  

### 4.2 Flow Diagram
```
[User] -- (click +/-) --> [View] -- (notify) --> [ViewModel] -- (update) --> [Repository] -- (writes) --> [Room Database]
                                               <-- (new data) -- [LiveData/StateFlow] <-- [ViewModel] <-- (observe) -- [View]
```

---

## 5. Data Persistence

1. **Entities**:
   - **ActionEntity**:  
     ```kotlin
     @Entity(tableName = "actions")
     data class ActionEntity(
       @PrimaryKey(autoGenerate = true) val actionId: Int = 0,
       val actionName: String,
       val creationTimestamp: Long
     )
     ```
   - **DayRecordEntity**:  
     ```kotlin
     @Entity(tableName = "day_records", primaryKeys = ["date", "actionId"])
     data class DayRecordEntity(
       val date: String,    // Store as "YYYY-MM-DD" or epoch day.
       val actionId: Int,
       val count: Int
     )
     ```

2. **DAO**:
   - `ActionDao` for CRUD on actions.  
   - `DayRecordDao` for reading/writing day counters.

3. **Database**:
   - Single `AppDatabase` implementing `RoomDatabase`.
   - Migrations as needed if schema changes.

4. **Behavior**:
   - Whenever user creates a new action, the app optionally pre-populates `DayRecordEntity` entries for all known days (or lazily creates them on first visit to each day).

---

## 6. App UI & UX Details

1. **Main Screen Layout**:
   - A list (RecyclerView or LazyColumn if Compose) of all **active actions** for the currently selected day.
   - Each item: 
     - “-” button (left)
     - Action name (top-center) + numeric count (center below name)
     - “+” button (right)

2. **Adding a New Action**:
   - A floating action button (FAB) or a button in the toolbar that opens a dialog or a new screen.
   - The user inputs the new action name. Presses “Add”. 
   - The new action appears in the day list *immediately*.

3. **Day Navigation**:
   - A top bar with “<” and “>” icons to move to previous/next day, or a date picker (MaterialDatePicker). 
   - The selected date is displayed in a text view.

4. **Visual Feedback**:
   - Buttons visually press in with a ripple effect on tap.
   - Count text updates instantly with a short fade or scale animation.

5. **Accessibility**:
   - Content descriptions on “+” and “-” buttons (e.g., “Increment water count”).
   - Font sizes respond to user preferences.

---

## 7. Project Structure

A recommended structure (module: `app`):

```
ActionTracker/
 ┣ .gradle/...
 ┣ gradle/...
 ┣ gradle.properties
 ┣ settings.gradle.kts
 ┣ build.gradle.kts        // Root build script
 ┣ libs.versions.toml      // Version catalog
 ┗ app/
    ┣ build.gradle.kts     // App module Gradle config
    ┣ src/
    │  ┣ main/
    │  │  ┣ AndroidManifest.xml
    │  │  ┣ java/com/example/actiontracker/
    │  │  │  ┣ ui/...
    │  │  │  ┣ data/...
    │  │  │  ┣ models/...
    │  │  │  ┗ ...
    │  │  ┗ res/
    │  │     ┗ layout/...
    │  ┣ test/...
    │  ┗ androidTest/...
    ┗ proguard-rules.pro  // Additional rules if needed
```

> **Note**: Make sure to rename `com.example.actiontracker` to a unique package name.

---

## 8. Build Configuration (Gradle)

Below are **example** configuration files. You can copy/paste them—but **be sure** to adjust the placeholders (e.g., package name, keystore info, version codes) for your own environment.

### 8.1 The Gradle Wrapper
**File**: `gradlew`, `gradlew.bat`, and the `gradle/wrapper/gradle-wrapper.properties`

```properties
# gradle-wrapper.properties

distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```
> **Tip**: The wrapper ensures consistent Gradle version (8.2) across all machines.

### 8.2 `settings.gradle.kts` Example

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("./libs.versions.toml"))
        }
    }
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ActionTracker"
include(":app")
```

> **Note**: `rootProject.name` can be anything. `include(":app")` references your module.

### 8.3 `libs.versions.toml` (Version Catalog)

Create **`libs.versions.toml`** in the project root:

```toml
[versions]
kotlin = "1.8.21"
agp = "8.0.2"  # Android Gradle Plugin
coreKtx = "1.10.1"
appcompat = "1.6.1"
material = "1.9.0"
constraintlayout = "2.1.4"
lifecycle = "2.6.1"
room = "2.5.1"
coroutines = "1.6.4"
junit = "4.13.2"
espresso = "3.5.1"
gradlePlayPublisher = "3.8.4" # For publishing AAB to Play (optional)

[libraries]
core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
constraintlayout = { group = "androidx.constraintlayout", name = "constraintlayout", version.ref = "constraintlayout" }
lifecycle-viewmodel = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycle" }
lifecycle-livedata = { group = "androidx.lifecycle", name = "lifecycle-livedata-ktx", version.ref = "lifecycle" }
lifecycle-runtime = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycle" }
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }

[plugins]
kotlinAndroid = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
androidApplication = { id = "com.android.application", version.ref = "agp" }
kotlinKapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }
gradlePlayPublisher = { id = "com.github.triplet.play", version.ref = "gradlePlayPublisher" }  # optional
```

### 8.4 Root `build.gradle.kts` Example

```kotlin
// build.gradle.kts (Root Project)
plugins {
    // No direct plugins here typically. 
    // If needed, e.g., for GPP plugin, we can add: id("com.github.triplet.play") version "3.8.4" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

> **Tip**: Some teams prefer to keep the root build script minimal. Dependencies and plugin versions live in the version catalog.

### 8.5 `gradle.properties` Example

```properties
# gradle.properties
# ----------------
# Set JVM settings to avoid OOM
org.gradle.jvmargs=-Xmx2g -Dfile.encoding=UTF-8

# Enable parallel build
org.gradle.parallel=true

# Enable caching
org.gradle.caching=true

# Use the new Gradle configuration cache (optional, still experimental)
org.gradle.configuration-cache=true

# Kotlin Settings
kotlin.code.style=official

# If you plan to sign from local properties
# (Better practice: use environment variables in CI for these)
RELEASE_STORE_FILE=../keystore/release.jks
RELEASE_STORE_PASSWORD=myKeystorePass
RELEASE_KEY_ALIAS=actionTracker
RELEASE_KEY_PASSWORD=myKeyAliasPass

# Example of enabling R8 full mode (optional):
android.enableR8.fullMode=true
```

### 8.6 App Module `build.gradle.kts` Example

```kotlin
// app/build.gradle.kts
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinKapt)
    // alias(libs.plugins.gradlePlayPublisher) // If deploying directly from Gradle to Play
}

android {
    namespace = "com.example.actiontracker" // TODO: Change to your unique package
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.actiontracker" // TODO: Change here too
        minSdk = 21
        targetSdk = 33
        versionCode = 1        // TODO: Automate or increment as needed
        versionName = "1.0.0"  // TODO: Automate or update for each release

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("releaseSigning") {
            // Usually replaced by environment variables in CI
            storeFile = file(System.getenv("RELEASE_STORE_FILE") ?: project.property("RELEASE_STORE_FILE"))
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

            // Optional advanced startup improvements:
            enableProfileBaselineProfile = true
        }
        debug {
            // Typically no signing config in debug
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    // If you want separate productFlavors, you can define them like:
    // flavorDimensions += "default"
    // productFlavors {
    //     create("dev") {
    //         dimension = "default"
    //         applicationIdSuffix = ".dev"
    //         versionNameSuffix = "-dev"
    //     }
    //     create("prod") {
    //         dimension = "default"
    //     }
    // }

    // Experimental if you want ViewBinding or DataBinding
    buildFeatures {
        viewBinding = true
        // dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources.excludes.add("META-INF/LICENSE*")
        resources.excludes.add("META-INF/NOTICE*")
    }
}

dependencies {
    implementation(libs.core-ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    // Jetpack Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)

    // Coroutines (if needed)
    implementation(libs.coroutines.android)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt("androidx.room:room-compiler:${libs.versions.room.get()}")

    // Unit Testing
    testImplementation("junit:junit:${libs.versions.junit.get()}")

    // Android Instrumented Testing
    androidTestImplementation("androidx.test.espresso:espresso-core:${libs.versions.espresso.get()}")
    androidTestImplementation("androidx.test:runner:1.5.2")

    // (Optional) If using Gradle Play Publisher to upload from CLI:
    // implementation("com.github.triplet.gradle:play-publisher:${libs.versions.gradlePlayPublisher.get()}")
}
```

> **Remember** to remove or modify any placeholders (keystore passwords, debug suffix, etc.) to match your real environment.

### 8.7 ProGuard / R8 Rules (Consumer Rules)

In `app/proguard-rules.pro` (or `consumer-rules.pro`, depending on your setup), you might add rules for Reflection-based libraries. For example:

```proguard
# Keep Room model classes
# Typically AGP auto-adds these, but if needed:
-keep class androidx.room.** { *; }

# If using Kotlin reflection or coroutines:
-keepclassmembers class kotlinx.coroutines.** { *; }

# Keep application classes with @Keep
-keep @interface androidx.annotation.Keep
```

> If you face runtime issues with minification, add or tweak rules here.  

---

## 9. Testing & Quality Assurance

1. **Unit Tests** (JUnit4/JUnit5):
   - Test the ViewModel logic for increment/decrement.
   - Test repository for correct DB writes and reads (Room Instrumentation test in `androidTest`).
2. **UI Tests** (Espresso or Compose UI test):
   - Launch the main screen, add an action, increment/decrement counters, verify displayed counts.
3. **Lint & Static Analysis**:
   - Run `./gradlew lint` or `./gradlew detekt` (if you add the Detekt plugin).
   - Fix or baseline any issues to ensure code quality.
4. **Coverage**:
   - Use Jacoco or Gradle’s built-in coverage for unit tests. Aim for 80%+ coverage if feasible.

---

## 10. Release & Publishing Workflow

1. **Signing**:
   - Ensure you have a **release keystore**. 
   - Store the keystore file in a secure location. 
   - Reference it in `signingConfigs` (like the example).
2. **Generate a Signed APK**:
   - Command line: `./gradlew assembleRelease`
   - Output is typically at `app/build/outputs/apk/release/app-release.apk`.
3. **Generate a Signed AAB**:
   - Command line: `./gradlew bundleRelease`
   - Output is typically at `app/build/outputs/bundle/release/app-release.aab`.
4. **Publish to Play Store**:
   - Manually upload the AAB through Play Console **or**  
   - Automate with the [Gradle Play Publisher plugin](https://github.com/Triple-T/gradle-play-publisher). 
   - For automation, set up a service account in the Play Console and specify JSON credentials in `gradle.properties` or environment variables.

---

## 11. Production Checklist

- [ ] **Application ID**: Replaced `com.example.actiontracker` with a unique domain name.  
- [ ] **Keystore**: Created and secured in a safe location. Updated `storeFile`, `keyAlias`, etc.  
- [ ] **Version Code/Name**: Bumped appropriately for each release.  
- [ ] **Release Notes & Privacy Policy**: Provided in the Play Console.  
- [ ] **ProGuard**: Verified no reflection-related crashes in release builds.  
- [ ] **Tested on multiple devices/emulators** (screen sizes, OS versions).  
- [ ] **Crashlytics** (optional but recommended).  
- [ ] **Minify & Resource Shrink**: Confirmed final APK size.  
- [ ] **Accessibility**: Verified with TalkBack, large fonts, etc.

---

## 12. Future Enhancements

1. **Cloud Sync**:
   - Offload data to Firebase or a custom backend for multi-device usage.
2. **Reminders**:
   - Notification system for daily action reminders.
3. **Analytics / Statistics**:
   - Graphical summary of how often actions are performed (weekly, monthly).
4. **Internationalization**:
   - Multi-language support, date-locale formatting.
5. **Advanced Onboarding**:
   - Show a tutorial or wizard for first-time app usage.

---

## Final Notes

- **Security**: never commit or share keystore passwords in plain text.  
- **Versioning**: keep your dependencies up-to-date with Dependabot or Renovate.  
- **Modularization**: as the project grows, consider splitting out features into separate modules.  

> By following the above instructions exactly (copying the Gradle config, adjusting placeholders, and implementing the MVVM flow with Room), a junior developer or automated agent should be able to compile, test, and produce a fully functional, signed release.  
