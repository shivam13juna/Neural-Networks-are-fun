

## 1 Navigation & Screen Map

```
MainActivity
└─ HomeScreen         – lists playlists + Hourly Chime entry
   ├─ PlaylistScreen  – e.g. “Hindi Songs”
   │   ├─ ScheduleTab (Uniform)  – same times every day
   │   └─ ScheduleTab (Per-day)  – Mon … Sun tabs
   │        └─ HourGridSheet     – toggle hour-slots  (bottom-sheet)
   └─ ChimeScreen     – 24 × 7 grid of  ≤ 60 s clips
```

**Navigation** – use **`androidx.navigation.compose`** + **Accompanist Navigation-Animation** so every forward/back has a small slide-in / fade-in (Material Motion spec).

---

## 2 Visual Language & Key Components

| UI element                           | Jetpack Compose equivalent                                              | Notes                                                                       |
| ------------------------------------ | ----------------------------------------------------------------------- | --------------------------------------------------------------------------- |
| Segmented “Uniform / Per-day” switch | `SegmentedButtonRow` (Material 3)                                       | Sticks below the top AppBar                                                 |
| Weekday selector                     | `ScrollableTabRow`                                                      | Seven text tabs (Mon … Sun)                                                 |
| Hour toggles                         | 24-slot `LazyVerticalGrid` (3–4 cols) OR `LazyColumn` with timeline row | Each slot has **Track title** + **Switch**; press row to edit track         |
| Add / edit track                     | `ModalBottomSheet`                                                      | Sheet lets user pick existing file or upload; shows waveform + trim preview |
| Upload button                        | `FloatingActionButton` on bottom-sheet                                  | Launches SAF picker                                                         |
| Volume & misc settings               | `SettingsActivity` with `PreferenceScreen` or Compose-equivalent list   |                                                                             |

All surfaces follow **Material 3** color tokens with dynamic color (Android 12+) fallback to BK green/white palette.

---

## 3 Wireframes (ASCII)

### 3.1 Playlist Screen

```
┌ AppBar ─  Hindi Songs                         ⋮ ┐
│  SegmentedButtonRow  [ Uniform | Per-day ]     │
└──────────▲───────────▲──────────────────────────┘
           │           │
  (Uniform)│   (Per-day)─> ScrollableTabRow Mon-Sun
           │                    ↓
           │        ┌─────────────Playlist for Tue────────────┐
           │        │ HH:MM   Track                     toggle │
           │        │ 03:30   Amrit Vela Hudh           [on  ] │
           │        │ 04:00   Shiv Ki Yaad             [off ] │
           │        │ 05:45   Satya Hi Shiv Hei        [off ] │
           │        │  …                                   ▼ │
           │        └─────────────────────────────────────────┘
           │                                   +  FAB  ➕
```

`FAB` opens **HourGridSheet** (see 3.2).

### 3.2 HourGridSheet (bottom sheet)

```
────────── EDIT HOURS (Tue) ──────────
│ 00 │ 01 │ 02 │ 03 │ 04 │ 05 │ 06   │
│off│off│off│on │off│off│off │ …     │
│ …  rows …                            │
│ Selected: 03:00 – Amrit Vela Hudh    │
│  [ Change Track ]  [ Remove ]        │
└──────────────  Save  ────────────────┘
```

*Tap a cell*: toggles state. *Long-press*: pops track chooser.

### 3.3 Hourly Chime Screen

```
┌ AppBar ─ Hourly Chime                               ⋮ ┐
│ Weekday chips: Mon Tue Wed Thu Fri Sat Sun           │
│    (multi-select; filters grid)                      │
│ ┌────────────── 24-slot grid ──────────────────────┐ │
│ │ 00:00  clip-name.mp3          [on]              │ │
│ │ 01:00  — not set —           [off]             │ │
│ │ …                                             │ │
│ └───────────────────────────────────────────────┘ │
│            ⊕  Add / Upload clip (FAB)              │
└─────────────────────────────────────────────────────┘
```

---

## 4 Interaction Flow (Uniform → Per-day pivot)

1. **Default:** new playlist starts in *Uniform* mode – one schedule list persisted with `dayOfWeek = 0` (sentinel).
2. **User toggles → Per-day:**

   * Migration dialog: “Copy current schedule to all days?” **Yes / No**.
   * On **Yes**: duplicate rows for Mon-Sun. On **No**: clear each list.
3. **Switch back to Uniform:** merges distinct per-day times; if conflicts, prompt: *“Keep earliest time / Keep all (duplicates trimmed) / Cancel”*.

---

## 5 Compose Skeleton (key bits)

```kotlin
@Composable
fun PlaylistScreen(playlistId: Long, nav: NavController) {
    val vm: PlaylistViewModel = hiltViewModel()
    val uiState by vm.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(uiState.playlistName) }) },
        floatingActionButton = {
            if (uiState.showFab) FloatingActionButton(
                onClick = { vm.onFabClick() }) { Icon(Icons.Outlined.Add, null) }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            ScheduleModeSelector(
                mode = uiState.mode,
                onSelect = vm::onModeChange
            )
            when (uiState.mode) {
                ScheduleMode.UNIFORM ->
                    TrackList(tracks = uiState.uniformTracks,
                              onToggle = vm::onTrackToggle)
                ScheduleMode.PER_DAY ->
                    PerDayPager(
                        days = uiState.days,
                        onTrackToggle = vm::onTrackToggle,
                        onFabClick = vm::onFabClick
                    )
            }
        }
    }
}
```

`PerDayPager` → `HorizontalPager` (Accompanist) with 7 pages, each embeds `TrackList`.

---

## 6 State Model & ViewModel pointers

```kotlin
data class PlaylistUiState(
    val playlistName: String = "",
    val mode: ScheduleMode = ScheduleMode.UNIFORM,
    val uniformTracks: List<TrackItem> = emptyList(),
    val days: List<DaySchedule> = emptyList(),
    val showFab: Boolean = true
)

data class TrackItem(
    val id: Long,
    val title: String,
    val time: LocalTime,
    val enabled: Boolean
)

data class DaySchedule(
    val weekday: Int,                // 1–7
    val tracks: List<TrackItem>
)
```

ViewModel exposes:

```kotlin
fun onModeChange(new: ScheduleMode)
fun onTrackToggle(trackId: Long, enabled: Boolean)
fun onFabClick(day: Int? = null)  // null = uniform list
```

---

## 7 Colour / Typography Cheat-Sheet

* **Primary:** `#006837` (BK green)
* **Secondary:** `#00838F`
* Tone mapping via Material You (compose-material-3 `dynamicLightColorScheme()` fallback to above).
* Body Large 16 sp / Title Medium 18 sp / Headline Small 24 sp.
* Hour-slot cell: `Card` with `shape = RoundedCornerShape(4.dp)`;
  *enabled* = `ContainerColor = primaryContainer`, *disabled* = `surfaceVariant`.

---

## 8 Edge-case & UX Notes

* **Upload > 60 s** → trigger `AudioClipper.clipToOneMinute()` automatically, show snackbar “Trimmed to 1 min”.
* **Missing file** on playback → skip, raise notification “File not found”.
* **Do Not Disturb**: ask user to grant “Ignore DND” permission (dialog leads to settings).
* **TalkBack**: every toggle’s `contentDescription` = “03 colon 30 on Monday switch. Currently ON”.

---

### Hand-off summary (what to give engineers)

1. Wireframes + flow (above).
2. Palette & typography tokens.
3. Compose skeleton snippets.
4. Interaction specs (#4, #8).
5. Re-use the PRD’s architecture & versions – UI lives in `ui/*`.

