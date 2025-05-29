package com.bk.trafficcontrol.data.db.entity

import androidx.room.*
import com.bk.trafficcontrol.domain.model.PlaylistType

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: PlaylistType,
    val enabled: Boolean = true
)

@Entity(
    tableName = "tracks",
    foreignKeys = [ForeignKey(
        entity = PlaylistEntity::class,
        parentColumns = ["id"],
        childColumns = ["playlistId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("playlistId")]
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val playlistId: Long,
    val title: String,
    val uri: String,
    val durationSec: Int
)

@Entity(
    tableName = "schedules",
    foreignKeys = [ForeignKey(
        entity = TrackEntity::class,
        parentColumns = ["id"],
        childColumns = ["trackId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("trackId")]
)
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackId: Long,
    val dayOfWeek: Int, // 1=Monday, 7=Sunday
    val hour: Int,
    val minute: Int,
    val enabled: Boolean = true
)
