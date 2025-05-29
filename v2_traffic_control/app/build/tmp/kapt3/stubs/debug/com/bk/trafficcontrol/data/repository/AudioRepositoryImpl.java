package com.bk.trafficcontrol.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0096@\u00a2\u0006\u0002\u0010\rJ\u0016\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u0010H\u0096@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0012\u001a\u00020\n2\u0006\u0010\u0013\u001a\u00020\u0014H\u0096@\u00a2\u0006\u0002\u0010\u0015J\u0016\u0010\u0016\u001a\u00020\n2\u0006\u0010\u0017\u001a\u00020\u0018H\u0096@\u00a2\u0006\u0002\u0010\u0019J\u0014\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u001c0\u001bH\u0016J\u0014\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u001c0\u001bH\u0016J\u0018\u0010\u001e\u001a\u0004\u0018\u00010\f2\u0006\u0010\u001f\u001a\u00020\u0014H\u0096@\u00a2\u0006\u0002\u0010\u0015J\u0018\u0010 \u001a\n\u0012\u0006\u0012\u0004\u0018\u00010!0\u001b2\u0006\u0010\"\u001a\u00020\u0014H\u0016J\u001c\u0010#\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u001c0\u001b2\u0006\u0010$\u001a\u00020%H\u0016J\u0018\u0010&\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u001f\u001a\u00020\u0014H\u0096@\u00a2\u0006\u0002\u0010\u0015J\u001c\u0010\'\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u001c0\u001b2\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\u0018\u0010(\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u001f\u001a\u00020\u0014H\u0096@\u00a2\u0006\u0002\u0010\u0015J\u0018\u0010)\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010*0\u001b2\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\u001c\u0010+\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00180\u001c0\u001b2\u0006\u0010\"\u001a\u00020\u0014H\u0016J\u0016\u0010,\u001a\u00020\u00142\u0006\u0010\u000b\u001a\u00020\fH\u0096@\u00a2\u0006\u0002\u0010\rJ\u0016\u0010-\u001a\u00020\u00142\u0006\u0010\u000f\u001a\u00020\u0010H\u0096@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010.\u001a\u00020\u00142\u0006\u0010\u0017\u001a\u00020\u0018H\u0096@\u00a2\u0006\u0002\u0010\u0019J\u0016\u0010/\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0096@\u00a2\u0006\u0002\u0010\rJ\u0016\u00100\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u0010H\u0096@\u00a2\u0006\u0002\u0010\u0011J\u0016\u00101\u001a\u00020\n2\u0006\u0010\u0017\u001a\u00020\u0018H\u0096@\u00a2\u0006\u0002\u0010\u0019R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00062"}, d2 = {"Lcom/bk/trafficcontrol/data/repository/AudioRepositoryImpl;", "Lcom/bk/trafficcontrol/domain/repository/AudioRepository;", "playlistDao", "Lcom/bk/trafficcontrol/data/db/dao/PlaylistDao;", "trackDao", "Lcom/bk/trafficcontrol/data/db/dao/TrackDao;", "scheduleDao", "Lcom/bk/trafficcontrol/data/db/dao/ScheduleDao;", "(Lcom/bk/trafficcontrol/data/db/dao/PlaylistDao;Lcom/bk/trafficcontrol/data/db/dao/TrackDao;Lcom/bk/trafficcontrol/data/db/dao/ScheduleDao;)V", "deletePlaylist", "", "playlist", "Lcom/bk/trafficcontrol/domain/model/Playlist;", "(Lcom/bk/trafficcontrol/domain/model/Playlist;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteSchedule", "schedule", "Lcom/bk/trafficcontrol/domain/model/Schedule;", "(Lcom/bk/trafficcontrol/domain/model/Schedule;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteSchedulesByTrack", "trackId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteTrack", "track", "Lcom/bk/trafficcontrol/domain/model/Track;", "(Lcom/bk/trafficcontrol/domain/model/Track;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllActiveSchedules", "Lkotlinx/coroutines/flow/Flow;", "", "getAllPlaylists", "getPlaylistById", "id", "getPlaylistWithTracks", "Lcom/bk/trafficcontrol/domain/model/PlaylistWithTracks;", "playlistId", "getPlaylistsByType", "type", "Lcom/bk/trafficcontrol/domain/model/PlaylistType;", "getScheduleById", "getSchedulesByTrack", "getTrackById", "getTrackWithSchedules", "Lcom/bk/trafficcontrol/domain/model/TrackWithSchedules;", "getTracksByPlaylist", "insertPlaylist", "insertSchedule", "insertTrack", "updatePlaylist", "updateSchedule", "updateTrack", "app_debug"})
public final class AudioRepositoryImpl implements com.bk.trafficcontrol.domain.repository.AudioRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.bk.trafficcontrol.data.db.dao.PlaylistDao playlistDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.bk.trafficcontrol.data.db.dao.TrackDao trackDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.bk.trafficcontrol.data.db.dao.ScheduleDao scheduleDao = null;
    
    @javax.inject.Inject()
    public AudioRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.data.db.dao.PlaylistDao playlistDao, @org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.data.db.dao.TrackDao trackDao, @org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.data.db.dao.ScheduleDao scheduleDao) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.bk.trafficcontrol.domain.model.Playlist>> getAllPlaylists() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.bk.trafficcontrol.domain.model.Playlist>> getPlaylistsByType(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.PlaylistType type) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getPlaylistById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.bk.trafficcontrol.domain.model.Playlist> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object insertPlaylist(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Playlist playlist, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updatePlaylist(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Playlist playlist, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object deletePlaylist(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Playlist playlist, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.bk.trafficcontrol.domain.model.Track>> getTracksByPlaylist(long playlistId) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getTrackById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.bk.trafficcontrol.domain.model.Track> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object insertTrack(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Track track, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateTrack(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Track track, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object deleteTrack(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Track track, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.bk.trafficcontrol.domain.model.Schedule>> getSchedulesByTrack(long trackId) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.bk.trafficcontrol.domain.model.Schedule>> getAllActiveSchedules() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getScheduleById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.bk.trafficcontrol.domain.model.Schedule> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object insertSchedule(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Schedule schedule, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateSchedule(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Schedule schedule, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object deleteSchedule(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Schedule schedule, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object deleteSchedulesByTrack(long trackId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<com.bk.trafficcontrol.domain.model.PlaylistWithTracks> getPlaylistWithTracks(long playlistId) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<com.bk.trafficcontrol.domain.model.TrackWithSchedules> getTrackWithSchedules(long trackId) {
        return null;
    }
}