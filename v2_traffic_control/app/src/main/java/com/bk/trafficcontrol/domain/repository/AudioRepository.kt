package com.bk.trafficcontrol.domain.repository

import com.bk.trafficcontrol.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AudioRepository {
    // Playlists
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistsByType(type: PlaylistType): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun insertPlaylist(playlist: Playlist): Long
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)

    // Tracks
    fun getTracksByPlaylist(playlistId: Long): Flow<List<Track>>
    suspend fun getTrackById(id: Long): Track?
    suspend fun insertTrack(track: Track): Long
    suspend fun updateTrack(track: Track)
    suspend fun deleteTrack(track: Track)

    // Schedules
    fun getSchedulesByTrack(trackId: Long): Flow<List<Schedule>>
    fun getAllActiveSchedules(): Flow<List<Schedule>>
    suspend fun getScheduleById(id: Long): Schedule?
    suspend fun insertSchedule(schedule: Schedule): Long
    suspend fun updateSchedule(schedule: Schedule)
    suspend fun deleteSchedule(schedule: Schedule)
    suspend fun deleteSchedulesByTrack(trackId: Long)

    // Combined data
    fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks?>
    fun getTrackWithSchedules(trackId: Long): Flow<TrackWithSchedules?>
}
