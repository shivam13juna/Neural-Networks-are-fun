package com.bk.trafficcontrol.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.trafficcontrol.domain.model.Playlist
import com.bk.trafficcontrol.domain.model.PlaylistType
import com.bk.trafficcontrol.domain.model.Track
import com.bk.trafficcontrol.domain.repository.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.bk.trafficcontrol.domain.model.Schedule
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AudioRepository
) : ViewModel() {
    // Default schedule slots: hour, minute, raw resource (filename without extension)
    private val defaultScheduleSlots = listOf(
        Triple(3, 30, "amritvela_1"),
        Triple(4, 0, "shiv_ki_yaad_2"),
        Triple(5, 45, "satya_hi_shiv_3"),
        Triple(7, 0, "antarman_4"),
        Triple(10, 30, "nit_yaad_karo_5"),
        Triple(12, 0, "shiv_pita_ko_6"),
        Triple(17, 30, "yogi_bano_7"),
        Triple(19, 30, "shiv_ki_yaad_8"),
        Triple(21, 30, "prem_se_9")
    )

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadPlaylists()
        initializeDefaultPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            repository.getAllPlaylists().collect { playlists ->
                println("Loaded ${playlists.size} playlists")
                playlists.forEach { playlist ->
                    println("Playlist: ${playlist.name} (${playlist.type})")
                }
                // Enforce fixed order: Hindi, English, Music Only, Custom, Hourly Chime
                val typeOrder = listOf(
                    PlaylistType.HINDI,
                    PlaylistType.ENGLISH,
                    PlaylistType.MUSIC_ONLY,
                    PlaylistType.CUSTOM,
                    PlaylistType.HOURLY_CHIME
                )
                val ordered = typeOrder.flatMap { t -> playlists.filter { it.type == t } }
                _uiState.value = _uiState.value.copy(
                    playlists = ordered,
                    isLoading = false,
                    masterEnabled = ordered.isNotEmpty() && ordered.all { it.enabled }
                )
            }
        }
    }

    private fun initializeDefaultPlaylists() {
        viewModelScope.launch {
            // Check if default playlists exist, if not create them
            val existingPlaylists = repository.getAllPlaylists().first()
            
            if (existingPlaylists.isEmpty()) {
                println("No playlists found, creating default playlists...")
                createDefaultPlaylists()
            } else {
                println("Found ${existingPlaylists.size} existing playlists, cleaning old seeded slots")
                // Remove any leftover default schedule slots from non-Hindi playlists (from prior schema)
                existingPlaylists.filter { it.type != PlaylistType.HINDI }.forEach { playlist ->
                    // Remove any seeded slot tracks (case-insensitive match on raw resource name)
                    val tracks = repository.getTracksByPlaylist(playlist.id).first()
                    tracks.filter { track ->
                        defaultScheduleSlots.any { (_, _, rawName) ->
                            track.uri.contains("/raw/$rawName", ignoreCase = true)
                        }
                    }.forEach { track ->
                        // Deleting track cascades its schedules
                        repository.deleteTrack(track)
                    }
                }
            }
        }
    }

    private suspend fun createDefaultPlaylists() {
        val defaultPlaylists = listOf(
            Playlist(name = "Hindi", type = PlaylistType.HINDI),
            Playlist(name = "English", type = PlaylistType.ENGLISH),
            Playlist(name = "Music Only", type = PlaylistType.MUSIC_ONLY),
            Playlist(name = "Hourly Chime", type = PlaylistType.HOURLY_CHIME)
        )

        defaultPlaylists.forEach { playlist ->
            val playlistId = repository.insertPlaylist(playlist)
            
            // Add sample tracks for each playlist
            when (playlist.type) {
                PlaylistType.HINDI -> {
                    val hindiTracks = listOf(
                        Track(playlistId = playlistId, title = "Om Namah Shivaya", uri = "android.resource://com.bk.trafficcontrol/raw/om_namah_shivaya", durationSec = 180),
                        Track(playlistId = playlistId, title = "Gayatri Mantra", uri = "android.resource://com.bk.trafficcontrol/raw/gayatri_mantra", durationSec = 240),
                        Track(playlistId = playlistId, title = "Hanuman Chalisa", uri = "android.resource://com.bk.trafficcontrol/raw/hanuman_chalisa", durationSec = 300)
                    )
                    hindiTracks.forEach { repository.insertTrack(it) }
                }
                PlaylistType.ENGLISH -> {
                    val englishTracks = listOf(
                        Track(playlistId = playlistId, title = "Peace Meditation", uri = "android.resource://com.bk.trafficcontrol/raw/peace_meditation", durationSec = 200),
                        Track(playlistId = playlistId, title = "Spiritual Reflection", uri = "android.resource://com.bk.trafficcontrol/raw/spiritual_reflection", durationSec = 250)
                    )
                    englishTracks.forEach { repository.insertTrack(it) }
                }
                PlaylistType.MUSIC_ONLY -> {
                    val musicTracks = listOf(
                        Track(playlistId = playlistId, title = "Peaceful Flute", uri = "android.resource://com.bk.trafficcontrol/raw/peaceful_flute", durationSec = 150),
                        Track(playlistId = playlistId, title = "Nature Sounds", uri = "android.resource://com.bk.trafficcontrol/raw/nature_sounds", durationSec = 180)
                    )
                    musicTracks.forEach { repository.insertTrack(it) }
                }
                PlaylistType.HOURLY_CHIME -> {
                    val chimeTracks = listOf(
                        Track(playlistId = playlistId, title = "Bell Chime 9AM", uri = "android.resource://com.bk.trafficcontrol/raw/bell_9am", durationSec = 10),
                        Track(playlistId = playlistId, title = "Gong Chime 12PM", uri = "android.resource://com.bk.trafficcontrol/raw/gong_12pm", durationSec = 15)
                    )
                    chimeTracks.forEach { repository.insertTrack(it) }
                }
                PlaylistType.CUSTOM -> {
                    // Custom playlists start empty
                }
            }
            // Seed default schedule slots only for Hindi playlist
            if (playlist.type == PlaylistType.HINDI) {
                defaultScheduleSlots.forEach { (hour, minute, rawName) ->
                    // insert a track for scheduling slot
                    val title = rawName.replace('_', ' ')
                    val uri = "android.resource://com.bk.trafficcontrol/raw/$rawName"
                    val trackId = repository.insertTrack(
                        com.bk.trafficcontrol.domain.model.Track(
                            playlistId = playlistId,
                            title = title,
                            uri = uri,
                            durationSec = 0
                        )
                    )
                    // insert schedule entry with dayOfWeek=0 (uniform)
                    repository.insertSchedule(
                        com.bk.trafficcontrol.domain.model.Schedule(
                            trackId = trackId,
                            dayOfWeek = 0,
                            hour = hour,
                            minute = minute,
                            enabled = true
                        )
                    )
                }
            }
        }
    }

    fun togglePlaylistEnabled(playlist: Playlist) {
        viewModelScope.launch {
            repository.updatePlaylist(playlist.copy(enabled = !playlist.enabled))
        }
    }

    /**
     * Toggles all playlists on or off based on the master switch state.
     */
    fun toggleMasterEnabled() {
        viewModelScope.launch {
            val newState = !_uiState.value.masterEnabled
            repository.getAllPlaylists().first().forEach { playlist ->
                repository.updatePlaylist(playlist.copy(enabled = newState))
            }
        }
    }

    fun createCustomPlaylist(name: String) {
        viewModelScope.launch {
            val customPlaylist = Playlist(
                name = name,
                type = PlaylistType.CUSTOM
            )
            repository.insertPlaylist(customPlaylist)
        }
    }
}

data class HomeUiState(
    val playlists: List<Playlist> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    /** True if all playlists are enabled */
    val masterEnabled: Boolean = false
)
