package com.bk.trafficcontrol.ui.hourly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.trafficcontrol.domain.model.PlaylistType
import com.bk.trafficcontrol.domain.repository.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HourlyChimeViewModel @Inject constructor(
    private val repository: AudioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HourlyChimeUiState())
    val uiState: StateFlow<HourlyChimeUiState> = _uiState.asStateFlow()

    init {
        loadHourlyChimeData()
    }

    private fun loadHourlyChimeData() {
        viewModelScope.launch {
            repository.getPlaylistsByType(PlaylistType.HOURLY_CHIME).collect { playlists ->
                if (playlists.isNotEmpty()) {
                    val hourlyPlaylist = playlists.first()
                    repository.getTracksByPlaylist(hourlyPlaylist.id).collect { tracks ->
                        // Create a map of hour -> track for easy lookup
                        val hourlySlots = mutableMapOf<Int, String>()
                        tracks.forEach { track ->
                            // Extract hour from track title or metadata
                            // For now, assume track title contains hour info
                            val hour = extractHourFromTrackTitle(track.title)
                            if (hour != -1) {
                                hourlySlots[hour] = track.uri
                            }
                        }
                        
                        _uiState.value = _uiState.value.copy(
                            hourlySlots = hourlySlots,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun selectDay(day: Int) {
        _uiState.value = _uiState.value.copy(selectedDay = day)
    }

    fun selectHourSlot(hour: Int) {
        // Handle hour slot selection - could open audio picker
        _uiState.value = _uiState.value.copy(selectedHour = hour)
        
        // For now, just toggle the slot (add/remove audio)
        viewModelScope.launch {
            val currentSlots = _uiState.value.hourlySlots.toMutableMap()
            if (currentSlots.containsKey(hour)) {
                currentSlots.remove(hour)
            } else {
                currentSlots[hour] = "android.resource://com.bk.trafficcontrol/raw/chime_$hour"
            }
            _uiState.value = _uiState.value.copy(hourlySlots = currentSlots)
        }
    }

    fun toggleDayFilter(day: Int) {
        val currentSelectedDays = _uiState.value.selectedDays.toMutableSet()
        if (currentSelectedDays.contains(day)) {
            currentSelectedDays.remove(day)
        } else {
            currentSelectedDays.add(day)
        }
        _uiState.value = _uiState.value.copy(selectedDays = currentSelectedDays)
    }

    fun toggleHourSlot(hour: Int, enabled: Boolean) {
        viewModelScope.launch {
            val currentSlots = _uiState.value.hourlySlots.toMutableMap()
            if (enabled) {
                currentSlots[hour] = "chime_$hour.mp3"
            } else {
                currentSlots.remove(hour)
            }
            _uiState.value = _uiState.value.copy(hourlySlots = currentSlots)
        }
    }

    private fun extractHourFromTrackTitle(title: String): Int {
        // Simple extraction logic - in real app this would be more sophisticated
        return try {
            val regex = "\\b([01]?[0-9]|2[0-3])\\b".toRegex()
            val match = regex.find(title)
            match?.value?.toInt() ?: -1
        } catch (e: Exception) {
            -1
        }
    }
}

data class HourlyChimeUiState(
    val selectedDay: Int = 1, // Monday by default
    val selectedDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7), // All days selected by default
    val selectedHour: Int? = null,
    val hourlySlots: Map<Int, String> = emptyMap(), // Hour -> Audio URI
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
