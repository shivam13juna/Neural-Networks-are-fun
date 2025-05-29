package com.bk.trafficcontrol.data.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&\u00a8\u0006\t"}, d2 = {"Lcom/bk/trafficcontrol/data/db/AppDatabase;", "Landroidx/room/RoomDatabase;", "()V", "playlistDao", "Lcom/bk/trafficcontrol/data/db/dao/PlaylistDao;", "scheduleDao", "Lcom/bk/trafficcontrol/data/db/dao/ScheduleDao;", "trackDao", "Lcom/bk/trafficcontrol/data/db/dao/TrackDao;", "app_debug"})
@androidx.room.TypeConverters(value = {com.bk.trafficcontrol.data.db.Converters.class})
@androidx.room.Database(entities = {com.bk.trafficcontrol.data.db.entity.PlaylistEntity.class, com.bk.trafficcontrol.data.db.entity.TrackEntity.class, com.bk.trafficcontrol.data.db.entity.ScheduleEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    
    public AppDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.bk.trafficcontrol.data.db.dao.PlaylistDao playlistDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.bk.trafficcontrol.data.db.dao.TrackDao trackDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.bk.trafficcontrol.data.db.dao.ScheduleDao scheduleDao();
}