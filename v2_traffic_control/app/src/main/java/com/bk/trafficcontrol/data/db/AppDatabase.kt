package com.bk.trafficcontrol.data.db

import androidx.room.*
import com.bk.trafficcontrol.data.db.dao.PlaylistDao
import com.bk.trafficcontrol.data.db.dao.TrackDao
import com.bk.trafficcontrol.data.db.dao.ScheduleDao
import com.bk.trafficcontrol.data.db.entity.PlaylistEntity
import com.bk.trafficcontrol.data.db.entity.TrackEntity
import com.bk.trafficcontrol.data.db.entity.ScheduleEntity
import com.bk.trafficcontrol.domain.model.PlaylistType

@TypeConverters(Converters::class)
@Database(
    entities = [PlaylistEntity::class, TrackEntity::class, ScheduleEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackDao(): TrackDao
    abstract fun scheduleDao(): ScheduleDao
}

class Converters {
    @TypeConverter
    fun fromPlaylistType(type: PlaylistType): String = type.name

    @TypeConverter
    fun toPlaylistType(name: String): PlaylistType = PlaylistType.valueOf(name)
}
