package com.bk.trafficcontrol.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.trafficcontrol.domain.model.Track
import com.bk.trafficcontrol.domain.model.Schedule
import com.bk.trafficcontrol.domain.repository.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

enum class ScheduleMode {
    UNIFORM,  // Same times every day
    PER_DAY   // Different times per day (Mon-Sun tabs)
}

data class TrackItem(
    val id: Long,
    val title: String,
    val time: LocalTime,
    val enabled: Boolean,
    val track: Track // Original track data
)

data class DaySchedule(
    val weekday: Int,  // 1-7 (Mon-Sun)
    val tracks: List<TrackItem>
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: AudioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()
    
    private var currentPlaylistId: Long? = null

    // Remove initial automatic load to avoid defaulting to Hindi; load only on setPlaylistId
    
    fun setPlaylistId(playlistId: Long) {
        if (currentPlaylistId != playlistId) {
            currentPlaylistId = playlistId
            println("Setting playlist ID: $playlistId")
            loadTracks()
        }
    }

    private fun loadTracks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            if (currentPlaylistId == null) {
                // Wait until setPlaylistId is called by UI
                return@launch
            }
            println("Loading tracks for playlist: $currentPlaylistId")
            // Get playlist name
            val playlist = repository.getAllPlaylists().first().find { it.id == currentPlaylistId }
            val playlistName = playlist?.name ?: "Unknown Playlist"
            _uiState.value = _uiState.value.copy(playlistName = playlistName)
            // Load tracks based on current mode
            loadTracksForMode(_uiState.value.mode)
        }
    }

    fun addSampleTrack() {
        viewModelScope.launch {
            currentPlaylistId?.let { playlistId ->
                val sampleTrack = Track(
                    playlistId = playlistId,
                    title = "Sample Track ${System.currentTimeMillis()}",
                    uri = "android.resource://com.bk.trafficcontrol/raw/sample_audio",
                    durationSec = 30
                )
                repository.insertTrack(sampleTrack)
            }
        }
    }

    fun onModeChange(newMode: ScheduleMode) {
        viewModelScope.launch {
            val oldMode = _uiState.value.mode
            _uiState.value = _uiState.value.copy(mode = newMode)
            
            // If switching to uniform mode, clean up any inconsistent schedules
            if (newMode == ScheduleMode.UNIFORM && oldMode == ScheduleMode.PER_DAY) {
                cleanupForUniformMode()
            }
            
            loadTracksForMode(newMode)
        }
    }
    
    private suspend fun cleanupForUniformMode() {
        try {
            currentPlaylistId?.let { playlistId ->
                val tracks = repository.getTracksByPlaylist(playlistId).first()
                
                tracks.forEach { track ->
                    val schedules = repository.getSchedulesByTrack(track.id).first()
                    if (schedules.isNotEmpty()) {
                        // Find the most common time and enabled state
                        val enabledCount = schedules.count { it.enabled }
                        val shouldBeEnabled = enabledCount > schedules.size / 2
                        
                        // Use the time from the first enabled schedule, or first schedule
                        val referenceSchedule = schedules.firstOrNull { it.enabled } 
                            ?: schedules.firstOrNull()
                        
                        if (referenceSchedule != null) {
                            val referenceTime = LocalTime.of(referenceSchedule.hour, referenceSchedule.minute)
                            synchronizeUniformSchedules(track.id, shouldBeEnabled, referenceTime)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("Error cleaning up for uniform mode: ${e.message}")
        }
    }

    fun onDaySelected(day: Int) {
        _uiState.value = _uiState.value.copy(selectedDay = day)
    }

    fun onTrackToggle(trackId: Long, enabled: Boolean) {
        viewModelScope.launch {
            val currentMode = _uiState.value.mode
            val currentDay = _uiState.value.selectedDay
            
            if (currentMode == ScheduleMode.UNIFORM) {
                // In uniform mode, synchronize all days to have the same state
                val allSchedules = repository.getSchedulesByTrack(trackId).first()
                val referenceSchedule = allSchedules.firstOrNull()
                val referenceTime = if (referenceSchedule != null) {
                    LocalTime.of(referenceSchedule.hour, referenceSchedule.minute)
                } else {
                    LocalTime.of(9, 0)
                }
                
                synchronizeUniformSchedules(trackId, enabled, referenceTime)
            } else {
                // In per-day mode, toggle only the specific day
                toggleTrackForDay(trackId, currentDay, enabled)
            }
            
            loadTracksForMode(currentMode)
        }
    }

    private suspend fun toggleTrackForDay(trackId: Long, day: Int, enabled: Boolean) {
        try {
            val allSchedules = repository.getSchedulesByTrack(trackId).first()
            val daySchedules = allSchedules.filter { it.dayOfWeek == day }
            
            if (daySchedules.isEmpty() && enabled) {
                // Create new schedule for this day
                val schedule = Schedule(
                    trackId = trackId,
                    dayOfWeek = day,
                    hour = 9,
                    minute = 0,
                    enabled = true
                )
                repository.insertSchedule(schedule)
            } else if (daySchedules.isNotEmpty()) {
                // Update existing schedules for this day
                daySchedules.forEach { schedule ->
                    repository.updateSchedule(schedule.copy(enabled = enabled))
                }
            }
        } catch (e: Exception) {
            println("Error toggling track schedule: ${e.message}")
        }
    }

    private suspend fun loadTracksForMode(mode: ScheduleMode) {
        currentPlaylistId?.let { playlistId ->
            val tracks = repository.getTracksByPlaylist(playlistId).first()
            
            if (mode == ScheduleMode.UNIFORM) {
                loadUniformTracks(tracks)
            } else {
                loadPerDayTracks(tracks)
            }
        }
    }

    private suspend fun loadUniformTracks(tracks: List<Track>) {
        // Only include tracks that have at least one schedule (seeded slots)
        val trackItems = tracks.mapNotNull { track ->
            val schedules = repository.getSchedulesByTrack(track.id).first()
            if (schedules.isEmpty()) return@mapNotNull null
            // Use the first enabled schedule or any schedule for time reference
            val referenceSchedule = schedules.firstOrNull { it.enabled }
                ?: schedules.firstOrNull()
            // Determine enabled state: track is on if any schedule is enabled uniformly
            val enabledCount = schedules.count { it.enabled }
            val isUniformlyEnabled = enabledCount == schedules.size || enabledCount == 0
            val trackEnabled = enabledCount > 0 && isUniformlyEnabled
            TrackItem(
                id = track.id,
                title = track.title,
                time = LocalTime.of(
                    referenceSchedule?.hour ?: 9,
                    referenceSchedule?.minute ?: 0
                ),
                enabled = trackEnabled,
                track = track
            )
        }
        // Sort tracks by scheduled time ascending (3:30 AM, 4:00 AM, ...)
        val sortedItems = trackItems.sortedBy { it.time }
        _uiState.value = _uiState.value.copy(
            uniformTracks = sortedItems,
            isLoading = false
        )
    }

    private suspend fun loadPerDayTracks(tracks: List<Track>) {
        val daySchedules = (1..7).map { day ->
            val dayTracks = tracks.map { track ->
                val schedules = repository.getSchedulesByTrack(track.id).first()
                    .filter { it.dayOfWeek == day }
                val firstSchedule = schedules.firstOrNull()
                
                TrackItem(
                    id = track.id,
                    title = track.title,
                    time = if (firstSchedule != null) {
                        LocalTime.of(firstSchedule.hour, firstSchedule.minute)
                    } else {
                        LocalTime.of(9, 0)
                    },
                    enabled = schedules.any { it.enabled },
                    track = track
                )
            }
            
            DaySchedule(weekday = day, tracks = dayTracks)
        }
        
        _uiState.value = _uiState.value.copy(
            days = daySchedules,
            isLoading = false
        )
    }

    fun toggleTrackSchedule(track: Track) {
        viewModelScope.launch {
            try {
                // Check if track has any schedules (get current state once)
                val scheduleList = repository.getSchedulesByTrack(track.id).first()
                
                if (scheduleList.isEmpty()) {
                    // Create a default schedule for weekdays at 9 AM
                    for (day in 1..5) { // Monday to Friday
                        val schedule = Schedule(
                            trackId = track.id,
                            dayOfWeek = day,
                            hour = 9,
                            minute = 0,
                            enabled = true
                        )
                        repository.insertSchedule(schedule)
                    }
                    println("Created schedules for track: ${track.title}")
                } else {
                    // Toggle the enabled state of existing schedules
                    scheduleList.forEach { schedule ->
                        repository.updateSchedule(schedule.copy(enabled = !schedule.enabled))
                    }
                    println("Toggled schedules for track: ${track.title}")
                }
            } catch (e: Exception) {
                println("Error toggling schedule: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private suspend fun synchronizeUniformSchedules(trackId: Long, enabled: Boolean, referenceTime: LocalTime? = null) {
        try {
            val allSchedules = repository.getSchedulesByTrack(trackId).first()
            val timeToUse = referenceTime ?: LocalTime.of(9, 0)
            
            // For uniform mode, ensure all days have the same schedule state
            for (day in 1..7) {
                val daySchedules = allSchedules.filter { it.dayOfWeek == day }
                
                if (daySchedules.isEmpty() && enabled) {
                    // Create schedule for this day
                    val schedule = Schedule(
                        trackId = trackId,
                        dayOfWeek = day,
                        hour = timeToUse.hour,
                        minute = timeToUse.minute,
                        enabled = enabled
                    )
                    repository.insertSchedule(schedule)
                } else if (daySchedules.isNotEmpty()) {
                    // Update existing schedules to match uniform state
                    daySchedules.forEach { schedule ->
                        repository.updateSchedule(
                            schedule.copy(
                                enabled = enabled,
                                hour = timeToUse.hour,
                                minute = timeToUse.minute
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            println("Error synchronizing uniform schedules: ${e.message}")
        }
    }
}

data class ScheduleUiState(
    val playlistName: String = "",
    val mode: ScheduleMode = ScheduleMode.UNIFORM,
    val uniformTracks: List<TrackItem> = emptyList(),
    val days: List<DaySchedule> = emptyList(),
    val selectedDay: Int = 1, // Monday by default
    val tracks: List<Track> = emptyList(), // Keep for backward compatibility
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val showFab: Boolean = true
)
