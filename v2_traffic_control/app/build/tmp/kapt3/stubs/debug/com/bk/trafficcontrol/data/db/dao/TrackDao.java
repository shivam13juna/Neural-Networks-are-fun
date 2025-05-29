package com.bk.trafficcontrol.data.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0007\u001a\u0004\u0018\u00010\u00052\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u001c\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\f2\u0006\u0010\u000e\u001a\u00020\tH\'J\u0016\u0010\u000f\u001a\u00020\t2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0010\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/bk/trafficcontrol/data/db/dao/TrackDao;", "", "deleteTrack", "", "track", "Lcom/bk/trafficcontrol/data/db/entity/TrackEntity;", "(Lcom/bk/trafficcontrol/data/db/entity/TrackEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTrackById", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTracksByPlaylist", "Lkotlinx/coroutines/flow/Flow;", "", "playlistId", "insertTrack", "updateTrack", "app_debug"})
@androidx.room.Dao()
public abstract interface TrackDao {
    
    @androidx.room.Query(value = "SELECT * FROM tracks WHERE playlistId = :playlistId ORDER BY title")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.bk.trafficcontrol.data.db.entity.TrackEntity>> getTracksByPlaylist(long playlistId);
    
    @androidx.room.Query(value = "SELECT * FROM tracks WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTrackById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.bk.trafficcontrol.data.db.entity.TrackEntity> $completion);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertTrack(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.data.db.entity.TrackEntity track, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateTrack(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.data.db.entity.TrackEntity track, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteTrack(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.data.db.entity.TrackEntity track, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}