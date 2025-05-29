# Product-Requirements Document (PRD)

**Project:** Traffic Control V2 (Android)
**Prepared for:** Implementation team / agency
**Last updated:** 20 May 2025

---

## 1  Problem & Vision

The current Brahma Kumaris “Traffic Control” app schedules audio tracks once per day with a dated UI. The upgraded version must:

* Offer a modern, responsive, Material 3 UI (Jetpack Compose + animations).
* Allow per-day, per-time scheduling of playlists **and** an **hourly-chime** feature with a distinct ≤ 60 s track for every hour.
* Auto-trim any chime file longer than 60 s on upload.
* Remain battery-friendly, reliable after reboot, and simple enough for a proof-of-concept (POC) while leaving hooks for future expansion.

---

## 2  Key Functional Requirements (FR)

| ID    | Requirement                                                                                                                                                                             |
| ----- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| FR-01 | **Playlists page** shows Hindi, English, Music-Only, Custom (extensible) with master On/Off.                                                                                            |
| FR-02 | **Per-playlist schedule** screen lists tracks with: label, time, individual toggle, and day-of-week selector (Mon–Sun).                                                                 |
| FR-03 | **Hourly-chime module** (extra tab on Home): 24 slots/h and day-picker grid. Each slot can hold a ≤ 60 s audio clip.                                                                    |
| FR-04 | **Upload** (`ACTION_OPEN_DOCUMENT`) supports MP3/Ogg/WAV. If > 60 s, automatically clip to 60 s (fade-out last 2 s) using FFmpeg-Kit, store the trimmed version in app-private storage. |
| FR-05 | **Settings**: master volume, “Start on boot”, clip-length threshold (default = 60 s), default TC mode.                                                                                  |
| FR-06 | **Exact playback**: audio must trigger even in Doze and after reboot.                                                                                                                   |
| FR-07 | **Foreground Playback Service** with media notification + pause/stop.                                                                                                                   |
| FR-08 | **Data Backup/Restore** via `AutoBackup` (XML/DB).                                                                                                                                      |
| FR-09 | **Accessibility & localisation**: English strings provided; Hindi translation placeholder.                                                                                              |
| FR-10 | **Minimum Android 7.0 (API 24)**, **target API 34**.                                                                                                                                    |

---

## 3  Non-Functional Requirements (NFR)

* **Reliability:** missed alarms ≤ 0.5 % in 30-day run.
* **Battery:** no excessive wake-ups (≤ 1 wake-lock / scheduled play).
* **Performance:** UI first-draw < 400 ms, schedule list scroll > 60 fps on Pixel 4a.
* **Security:** app-private storage; no external WRITE permission; follow scoped storage.
* **Scalability:** table schema supports arbitrary playlists/tracks.

---

## 4  Technical Stack & Pinned Versions

| Layer             | Library / Tool                       | Version (May 2025) | Remarks                                         |
| ----------------- | ------------------------------------ | ------------------ | ----------------------------------------------- |
| Language          | **Kotlin**                           | 1.9.24             | Source-sets are 100 % Kotlin.                   |
| Build             | Android Gradle Plugin                | 8.2.2              | Requires JDK 17.                                |
| UI                | Jetpack **Compose BOM**              | 2025.05.00         | Pulls compose‐ui 1.6.x, material3 1.2.x.        |
| Animation helpers | **Accompanist**                      | 0.37.2             | For navigation-animation + permissions.         |
| Media             | **ExoPlayer**                        | 2.19.1             | Bundled core + ui.                              |
| Audio trim        | **FFmpeg-Kit-min-gpl**               | 4.5.LTS            | 28 MiB APK delta; used only for > 60 s uploads. |
| Schedulers        | **WorkManager**                      | 2.9.0              | Exact alarms on API 31+.                        |
| DI                | **Hilt**                             | 2.50               | `hiltNavigationCompose`.                        |
| DB                | **Room**                             | 2.6.1              | Kotlin-Flow/Coroutines support.                 |
| Serialization     | **Kotlinx-serialization JSON**       | 1.7.0              | Export/import backup.                           |
| Testing           | JUnit 5 / Turbine / Robolectric 4.11 |                    |                                                 |
| CI                | GitHub Actions + Gradle 8.8          |                    | Static analysis (Detekt 1.23).                  |

> **Gradle** wrapper `gradle-wrapper.properties` ⇒ `distributionUrl=https\://services.gradle.org/distributions/gradle-8.8-all.zip`

---

## 5  High-level Architecture

```
┌──────────────────────┐
│        UI (Compose)  │  ← ViewModels (Hilt)
└──────────┬───────────┘
           │
    Kotlin Flow + Coroutines
           │
┌──────────▼───────────┐
│    Domain Layer      │  ← UseCases (ScheduleTrack, PlayTrack…)
└──────────┬───────────┘
           │
┌──────────▼───────────┐
│   Data Layer         │  ← Room DB + Prefs + FileStore
│  (Repositories)      │
└──────┬───────┬───────┘
       │       │
  WorkManager  │
 (ScheduleWorker,│  BroadcastReceiver
  BootReceiver)  │
       │       │
┌──────▼───────▼──────┐
│ ForegroundService   │  ← ExoPlayer instance
└─────────────────────┘
```

---

## 6  Database Schema (Room)

```kotlin
@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: PlaylistType,          // HINDI, ENGLISH, HOURLY_CHIME …
    val enabled: Boolean = true
)

@Entity(
    tableName = "tracks",
    foreignKeys = [ForeignKey(
        entity = PlaylistEntity::class,
        parentColumns = ["id"],
        childColumns = ["playlistId"],
        onDelete = CASCADE
    )],
    indices = [Index("playlistId")]
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playlistId: Long,
    val title: String,
    val uri: String,
    val durationSec: Int
)

@Entity(indices = [Index("trackId")])
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val trackId: Long,
    val dayOfWeek: Int,   // 1=Mon … 7=Sun
    val hour: Int,
    val minute: Int,
    val enabled: Boolean = true
)
```

Hourly-chime uses `PlaylistType.HOURLY_CHIME` and always stores `minute=0`.

---

## 7  Scheduling Strategy

* **WorkManager** `OneTimeWorkRequest` scheduled for every active `ScheduleEntity`.
* On reboot or when user edits a schedule, a `Rescheduler` re-creates future work.
* For exact times on API 31+, use `setExact` via `AlarmManager` interop; before API 31, WorkManager’s `setInitialDelay` is sufficient (accuracy ±2 m under Doze).
* Hourly chime: generate 24 workers per enabled day; re-enqueue weekly.

---

## 8  Permissions & Manifest Snippets

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<!-- Scoped-storage compliant; READ_MEDIA_AUDIO on 33+, READ_EXTERNAL_STORAGE pre-33 -->
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

<application … android:exported="false">
    <service
        android:name=".service.AudioPlayerService"
        android:foregroundServiceType="mediaPlayback" />
    <receiver
        android:name=".receiver.BootReceiver"
        android:exported="true"
        android:enabled="true">
        <intent-filter>
            <action android:name="android.intent.action.BOOT_COMPLETED" />
        </intent-filter>
    </receiver>
</application>
```

---

## 9  Project / Module Folder Layout

```
TrafficControlV2/
├── build.gradle.kts            (root, version-catalog)
├── settings.gradle.kts
├── app/
│   ├── build.gradle.kts        (AGP + dependencies)
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/bk/trafficcontrol/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── App.kt                    (Compose Material3 theme)
│   │   │   │   ├── ui/
│   │   │   │   │   ├── home/…
│   │   │   │   │   ├── schedule/…
│   │   │   │   │   ├── hourly/…
│   │   │   │   ├── data/
│   │   │   │   │   ├── db/AppDatabase.kt
│   │   │   │   │   ├── dao/PlaylistDao.kt
│   │   │   │   │   ├── repo/AudioRepositoryImpl.kt
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/…
│   │   │   │   │   ├── usecase/ScheduleTrackUseCase.kt
│   │   │   │   ├── service/AudioPlayerService.kt
│   │   │   │   ├── scheduler/ScheduleWorker.kt
│   │   │   │   ├── receiver/BootReceiver.kt
│   │   │   │   ├── util/AudioClipper.kt
│   │   │   │   ├── di/AppModule.kt
│   │   │   ├── res/
│   │   │   │   ├── drawable/…
│   │   │   │   ├── layout/  (only for notification custom view)
│   │   │   │   └── values/
│   │   └── test/…            (unit tests)
│   └── proguard-rules.pro
└── gradle/…
```

---

## 10  Code Skeleton (key files)

```kotlin
// App.kt
@HiltAndroidApp
class TCApplication : Application()

// MainActivity.kt
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TrafficControlApp() }
    }
}

// service/AudioPlayerService.kt
@AndroidEntryPoint
class AudioPlayerService : LifecycleService() {
    @Inject lateinit var player: ExoPlayer
    …
}

// scheduler/ScheduleWorker.kt
@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repo: AudioRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result { … }
}

// util/AudioClipper.kt
object AudioClipper {
    suspend fun clipToOneMinute(context: Context, source: Uri): Uri { … }
}

// ui/home/HomeScreen.kt (Compose)
@Composable
fun HomeScreen(nav: NavController, vm: HomeViewModel = hiltViewModel()) { … }
```

Each sub-package (`home`, `schedule`, `hourly`) follows *MVVM*:

* `XScreen.kt` (Composable)
* `XViewModel.kt` (state)

Repositories expose Kotlin `Flow` streams to viewmodels.

---

## 11  Gradle (app/build.gradle.kts) excerpts

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlinx-serialization")
}

android {
    namespace = "com.bk.trafficcontrol"
    compileSdk = 34
    defaultConfig {
        applicationId = namespace
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.6.10" }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.05.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Hilt + WorkManager
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.2.0")

    // ExoPlayer
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // Room
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // FFmpeg-Kit (audio clipping)
    implementation("com.arthenica:ffmpeg-kit-min-gpl:4.5.LTS")

    // Accompanist
    implementation("com.google.accompanist:accompanist-navigation-material:0.37.2")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
}
```

Version catalog (`gradle/libs.versions.toml`) can centralise numbers.

---

## 12  CI / CD

* **GitHub Actions:**

  * Build with cache, run unit tests, run `detekt`, produce signed release AAB via secret-stored keystore.
  * Upload artifact to internal Play track (if registered) using `gradle-play-publisher` 5.7.0.

---

## 13  Testing Matrix

| Layer       | Tool                                 | Example                               |
| ----------- | ------------------------------------ | ------------------------------------- |
| Unit        | JUnit 5 + Turbine                    | DAO, UseCases                         |
| UI          | `compose-ui-test`                    | verify schedule toggle updates DB     |
| Integration | Robolectric 4.11                     | ensure WorkManager enqueues correctly |
| End-to-end  | Firebase Test Lab (Pixel 6 / API 34) | playback reliability                  |

---

## 14  Future Enhancements (not in POC)

* Cloud sync of schedules.
* Wear OS companion tile.
* Adaptive volume by time-of-day.

---

### Hand-off Checklist

1. Clone repo with template structure above.
2. Insert app icon assets (512 × 512 png) in `res/mipmap-xxxhdpi`.
3. Replace `strings.xml` placeholders.
4. Add keystore + Play Console service-account JSON in GitHub secrets.
5. Run `./gradlew :app:assembleDebug` → verify APK plays hourly chime.

