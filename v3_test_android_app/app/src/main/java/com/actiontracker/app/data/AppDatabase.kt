package com.actiontracker.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.actiontracker.app.models.ActionEntity
import com.actiontracker.app.models.DayRecordEntity

@Database(entities = [ActionEntity::class, DayRecordEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun actionDao(): ActionDao
    abstract fun dayRecordDao(): DayRecordDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "action_tracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
