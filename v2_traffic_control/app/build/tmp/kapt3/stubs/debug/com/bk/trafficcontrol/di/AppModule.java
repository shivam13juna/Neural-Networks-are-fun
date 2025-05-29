package com.bk.trafficcontrol.di;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u0007J \u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0007J\u0012\u0010\u000f\u001a\u00020\u00102\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\u0011\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\u0004H\u0007J\u0010\u0010\u0013\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u0004H\u0007J\u0010\u0010\u0014\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\u0004H\u0007\u00a8\u0006\u0015"}, d2 = {"Lcom/bk/trafficcontrol/di/AppModule;", "", "()V", "provideAppDatabase", "Lcom/bk/trafficcontrol/data/db/AppDatabase;", "context", "Landroid/content/Context;", "provideAudioRepository", "Lcom/bk/trafficcontrol/domain/repository/AudioRepository;", "playlistDao", "Lcom/bk/trafficcontrol/data/db/dao/PlaylistDao;", "trackDao", "Lcom/bk/trafficcontrol/data/db/dao/TrackDao;", "scheduleDao", "Lcom/bk/trafficcontrol/data/db/dao/ScheduleDao;", "provideExoPlayer", "Lcom/google/android/exoplayer2/ExoPlayer;", "providePlaylistDao", "database", "provideScheduleDao", "provideTrackDao", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class AppModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.bk.trafficcontrol.di.AppModule INSTANCE = null;
    
    private AppModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.bk.trafficcontrol.data.db.AppDatabase provideAppDatabase(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.bk.trafficcontrol.data.db.dao.PlaylistDao providePlaylistDao(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.data.db.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.bk.trafficcontrol.data.db.dao.TrackDao provideTrackDao(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.data.db.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.bk.trafficcontrol.data.db.dao.ScheduleDao provideScheduleDao(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.data.db.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.bk.trafficcontrol.domain.repository.AudioRepository provideAudioRepository(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.data.db.dao.PlaylistDao playlistDao, @org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.data.db.dao.TrackDao trackDao, @org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.data.db.dao.ScheduleDao scheduleDao) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.exoplayer2.ExoPlayer provideExoPlayer(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
}