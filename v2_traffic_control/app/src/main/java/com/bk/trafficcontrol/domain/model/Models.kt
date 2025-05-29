package com.bk.trafficcontrol.domain.model

enum class PlaylistType {
    HINDI,
    ENGLISH,
    MUSIC_ONLY,
    CUSTOM,
    HOURLY_CHIME
}

data class Playlist(
    val id: Long = 0,
    val name: String,
    val type: PlaylistType,
    val enabled: Boolean = true
)

data class Track(
    val id: Long = 0,
    val playlistId: Long,
    val title: String,
    val uri: String,
    val durationSec: Int
)

data class Schedule(
    val id: Long = 0,
    val trackId: Long,
    val dayOfWeek: Int, // 1=Monday, 7=Sunday
    val hour: Int,
    val minute: Int,
    val enabled: Boolean = true
)

data class PlaylistWithTracks(
    val playlist: Playlist,
    val tracks: List<Track>
)

data class TrackWithSchedules(
    val track: Track,
    val schedules: List<Schedule>
)
