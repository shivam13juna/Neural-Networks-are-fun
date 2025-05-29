package com.bk.trafficcontrol.domain.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\rH\u00a6@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u000f\u001a\u00020\u00032\u0006\u0010\u0010\u001a\u00020\u0011H\u00a6@\u00a2\u0006\u0002\u0010\u0012J\u0014\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u00150\u0014H&J\u0014\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00150\u0014H&J\u0018\u0010\u0017\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0018\u001a\u00020\rH\u00a6@\u00a2\u0006\u0002\u0010\u000eJ\u0018\u0010\u0019\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001a0\u00142\u0006\u0010\u001b\u001a\u00020\rH&J\u001c\u0010\u001c\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00150\u00142\u0006\u0010\u001d\u001a\u00020\u001eH&J\u0018\u0010\u001f\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0018\u001a\u00020\rH\u00a6@\u00a2\u0006\u0002\u0010\u000eJ\u001c\u0010 \u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u00150\u00142\u0006\u0010\f\u001a\u00020\rH&J\u0018\u0010!\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0018\u001a\u00020\rH\u00a6@\u00a2\u0006\u0002\u0010\u000eJ\u0018\u0010\"\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010#0\u00142\u0006\u0010\f\u001a\u00020\rH&J\u001c\u0010$\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\u00150\u00142\u0006\u0010\u001b\u001a\u00020\rH&J\u0016\u0010%\u001a\u00020\r2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010&\u001a\u00020\r2\u0006\u0010\b\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\'\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u0011H\u00a6@\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010(\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010)\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010*\u001a\u00020\u00032\u0006\u0010\u0010\u001a\u00020\u0011H\u00a6@\u00a2\u0006\u0002\u0010\u0012\u00a8\u0006+"}, d2 = {"Lcom/bk/trafficcontrol/domain/repository/AudioRepository;", "", "deletePlaylist", "", "playlist", "Lcom/bk/trafficcontrol/domain/model/Playlist;", "(Lcom/bk/trafficcontrol/domain/model/Playlist;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteSchedule", "schedule", "Lcom/bk/trafficcontrol/domain/model/Schedule;", "(Lcom/bk/trafficcontrol/domain/model/Schedule;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteSchedulesByTrack", "trackId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteTrack", "track", "Lcom/bk/trafficcontrol/domain/model/Track;", "(Lcom/bk/trafficcontrol/domain/model/Track;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllActiveSchedules", "Lkotlinx/coroutines/flow/Flow;", "", "getAllPlaylists", "getPlaylistById", "id", "getPlaylistWithTracks", "Lcom/bk/trafficcontrol/domain/model/PlaylistWithTracks;", "playlistId", "getPlaylistsByType", "type", "Lcom/bk/trafficcontrol/domain/model/PlaylistType;", "getScheduleById", "getSchedulesByTrack", "getTrackById", "getTrackWithSchedules", "Lcom/bk/trafficcontrol/domain/model/TrackWithSchedules;", "getTracksByPlaylist", "insertPlaylist", "insertSchedule", "insertTrack", "updatePlaylist", "updateSchedule", "updateTrack", "app_debug"})
public abstract interface AudioRepository {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.bk.trafficcontrol.domain.model.Playlist>> getAllPlaylists();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.bk.trafficcontrol.domain.model.Playlist>> getPlaylistsByType(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.PlaylistType type);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPlaylistById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.bk.trafficcontrol.domain.model.Playlist> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertPlaylist(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Playlist playlist, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updatePlaylist(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Playlist playlist, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deletePlaylist(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Playlist playlist, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.bk.trafficcontrol.domain.model.Track>> getTracksByPlaylist(long playlistId);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTrackById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.bk.trafficcontrol.domain.model.Track> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertTrack(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Track track, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateTrack(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Track track, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteTrack(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Track track, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.bk.trafficcontrol.domain.model.Schedule>> getSchedulesByTrack(long trackId);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.bk.trafficcontrol.domain.model.Schedule>> getAllActiveSchedules();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getScheduleById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.bk.trafficcontrol.domain.model.Schedule> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertSchedule(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Schedule schedule, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateSchedule(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Schedule schedule, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteSchedule(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Schedule schedule, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteSchedulesByTrack(long trackId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.bk.trafficcontrol.domain.model.PlaylistWithTracks> getPlaylistWithTracks(long playlistId);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.bk.trafficcontrol.domain.model.TrackWithSchedules> getTrackWithSchedules(long trackId);
}