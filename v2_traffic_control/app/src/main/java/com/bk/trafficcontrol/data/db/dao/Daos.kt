package com.bk.trafficcontrol.data.db.dao

import androidx.room.*
import com.bk.trafficcontrol.data.db.entity.PlaylistEntity
import com.bk.trafficcontrol.data.db.entity.TrackEntity
import com.bk.trafficcontrol.data.db.entity.ScheduleEntity
import com.bk.trafficcontrol.domain.model.PlaylistType
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY name")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE type = :type")
    fun getPlaylistsByType(type: PlaylistType): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: Long): PlaylistEntity?

    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)
}

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks WHERE playlistId = :playlistId ORDER BY title")
    fun getTracksByPlaylist(playlistId: Long): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrackById(id: Long): TrackEntity?

    @Insert
    suspend fun insertTrack(track: TrackEntity): Long

    @Update
    suspend fun updateTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules WHERE trackId = :trackId ORDER BY dayOfWeek, hour, minute")
    fun getSchedulesByTrack(trackId: Long): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedules WHERE enabled = 1 ORDER BY dayOfWeek, hour, minute")
    fun getAllActiveSchedules(): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getScheduleById(id: Long): ScheduleEntity?

    @Insert
    suspend fun insertSchedule(schedule: ScheduleEntity): Long

    @Update
    suspend fun updateSchedule(schedule: ScheduleEntity)

    @Delete
    suspend fun deleteSchedule(schedule: ScheduleEntity)

    @Query("DELETE FROM schedules WHERE trackId = :trackId")
    suspend fun deleteSchedulesByTrack(trackId: Long)
}
