package com.bk.trafficcontrol.data.repository

import com.bk.trafficcontrol.data.db.dao.PlaylistDao
import com.bk.trafficcontrol.data.db.dao.TrackDao
import com.bk.trafficcontrol.data.db.dao.ScheduleDao
import com.bk.trafficcontrol.data.db.entity.PlaylistEntity
import com.bk.trafficcontrol.data.db.entity.TrackEntity
import com.bk.trafficcontrol.data.db.entity.ScheduleEntity
import com.bk.trafficcontrol.domain.model.*
import com.bk.trafficcontrol.domain.repository.AudioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val trackDao: TrackDao,
    private val scheduleDao: ScheduleDao
) : AudioRepository {

    // Playlist methods
    override fun getAllPlaylists(): Flow<List<Playlist>> =
        playlistDao.getAllPlaylists().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getPlaylistsByType(type: PlaylistType): Flow<List<Playlist>> =
        playlistDao.getPlaylistsByType(type).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getPlaylistById(id: Long): Playlist? =
        playlistDao.getPlaylistById(id)?.toDomain()

    override suspend fun insertPlaylist(playlist: Playlist): Long =
        playlistDao.insertPlaylist(playlist.toEntity())

    override suspend fun updatePlaylist(playlist: Playlist) =
        playlistDao.updatePlaylist(playlist.toEntity())

    override suspend fun deletePlaylist(playlist: Playlist) =
        playlistDao.deletePlaylist(playlist.toEntity())

    // Track methods
    override fun getTracksByPlaylist(playlistId: Long): Flow<List<Track>> =
        trackDao.getTracksByPlaylist(playlistId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getTrackById(id: Long): Track? =
        trackDao.getTrackById(id)?.toDomain()

    override suspend fun insertTrack(track: Track): Long =
        trackDao.insertTrack(track.toEntity())

    override suspend fun updateTrack(track: Track) =
        trackDao.updateTrack(track.toEntity())

    override suspend fun deleteTrack(track: Track) =
        trackDao.deleteTrack(track.toEntity())

    // Schedule methods
    override fun getSchedulesByTrack(trackId: Long): Flow<List<Schedule>> =
        scheduleDao.getSchedulesByTrack(trackId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getAllActiveSchedules(): Flow<List<Schedule>> =
        scheduleDao.getAllActiveSchedules().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getScheduleById(id: Long): Schedule? =
        scheduleDao.getScheduleById(id)?.toDomain()

    override suspend fun insertSchedule(schedule: Schedule): Long =
        scheduleDao.insertSchedule(schedule.toEntity())

    override suspend fun updateSchedule(schedule: Schedule) =
        scheduleDao.updateSchedule(schedule.toEntity())

    override suspend fun deleteSchedule(schedule: Schedule) =
        scheduleDao.deleteSchedule(schedule.toEntity())

    override suspend fun deleteSchedulesByTrack(trackId: Long) =
        scheduleDao.deleteSchedulesByTrack(trackId)

    // Combined data methods
    override fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks?> =
        combine(
            playlistDao.getAllPlaylists(),
            trackDao.getTracksByPlaylist(playlistId)
        ) { playlists: List<PlaylistEntity>, tracks: List<TrackEntity> ->
            val playlist = playlists.find { it.id == playlistId }?.toDomain()
            playlist?.let {
                PlaylistWithTracks(
                    playlist = it,
                    tracks = tracks.map { track -> track.toDomain() }
                )
            }
        }

    override fun getTrackWithSchedules(trackId: Long): Flow<TrackWithSchedules?> =
        combine(
            flow { emit(trackDao.getTrackById(trackId)) },
            scheduleDao.getSchedulesByTrack(trackId)
        ) { track: TrackEntity?, schedules: List<ScheduleEntity> ->
            track?.let {
                TrackWithSchedules(
                    track = it.toDomain(),
                    schedules = schedules.map { schedule -> schedule.toDomain() }
                )
            }
        }
}

// Extension functions for mapping between domain and entity models
private fun PlaylistEntity.toDomain() = Playlist(
    id = id,
    name = name,
    type = type,
    enabled = enabled
)

private fun Playlist.toEntity() = PlaylistEntity(
    id = id,
    name = name,
    type = type,
    enabled = enabled
)

private fun TrackEntity.toDomain() = Track(
    id = id,
    playlistId = playlistId,
    title = title,
    uri = uri,
    durationSec = durationSec
)

private fun Track.toEntity() = TrackEntity(
    id = id,
    playlistId = playlistId,
    title = title,
    uri = uri,
    durationSec = durationSec
)

private fun ScheduleEntity.toDomain() = Schedule(
    id = id,
    trackId = trackId,
    dayOfWeek = dayOfWeek,
    hour = hour,
    minute = minute,
    enabled = enabled
)

private fun Schedule.toEntity() = ScheduleEntity(
    id = id,
    trackId = trackId,
    dayOfWeek = dayOfWeek,
    hour = hour,
    minute = minute,
    enabled = enabled
)
