package com.bk.trafficcontrol.di

import android.content.Context
import androidx.room.Room
import com.bk.trafficcontrol.data.db.AppDatabase
import com.bk.trafficcontrol.data.db.dao.PlaylistDao
import com.bk.trafficcontrol.data.db.dao.TrackDao
import com.bk.trafficcontrol.data.db.dao.ScheduleDao
import com.bk.trafficcontrol.data.repository.AudioRepositoryImpl
import com.bk.trafficcontrol.domain.repository.AudioRepository
import com.google.android.exoplayer2.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "traffic_control_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePlaylistDao(database: AppDatabase): PlaylistDao = database.playlistDao()

    @Provides
    fun provideTrackDao(database: AppDatabase): TrackDao = database.trackDao()

    @Provides
    fun provideScheduleDao(database: AppDatabase): ScheduleDao = database.scheduleDao()

    @Provides
    @Singleton
    fun provideAudioRepository(
        playlistDao: PlaylistDao,
        trackDao: TrackDao,
        scheduleDao: ScheduleDao
    ): AudioRepository {
        return AudioRepositoryImpl(playlistDao, trackDao, scheduleDao)
    }

    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }
}
