package com.bk.trafficcontrol.ui.schedule;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u000f\u001a\u00020\u0010J\u000e\u0010\u0011\u001a\u00020\u0010H\u0082@\u00a2\u0006\u0002\u0010\u0012J\u001c\u0010\u0013\u001a\u00020\u00102\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015H\u0082@\u00a2\u0006\u0002\u0010\u0017J\b\u0010\u0018\u001a\u00020\u0010H\u0002J\u0016\u0010\u0019\u001a\u00020\u00102\u0006\u0010\u001a\u001a\u00020\u001bH\u0082@\u00a2\u0006\u0002\u0010\u001cJ\u001c\u0010\u001d\u001a\u00020\u00102\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015H\u0082@\u00a2\u0006\u0002\u0010\u0017J\u000e\u0010\u001e\u001a\u00020\u00102\u0006\u0010\u001f\u001a\u00020 J\u000e\u0010!\u001a\u00020\u00102\u0006\u0010\"\u001a\u00020\u001bJ\u0016\u0010#\u001a\u00020\u00102\u0006\u0010$\u001a\u00020\t2\u0006\u0010%\u001a\u00020&J\u000e\u0010\'\u001a\u00020\u00102\u0006\u0010(\u001a\u00020\tJ*\u0010)\u001a\u00020\u00102\u0006\u0010$\u001a\u00020\t2\u0006\u0010%\u001a\u00020&2\n\b\u0002\u0010*\u001a\u0004\u0018\u00010+H\u0082@\u00a2\u0006\u0002\u0010,J&\u0010-\u001a\u00020\u00102\u0006\u0010$\u001a\u00020\t2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010%\u001a\u00020&H\u0082@\u00a2\u0006\u0002\u0010.J\u000e\u0010/\u001a\u00020\u00102\u0006\u00100\u001a\u00020\u0016R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0012\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00070\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u00061"}, d2 = {"Lcom/bk/trafficcontrol/ui/schedule/ScheduleViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/bk/trafficcontrol/domain/repository/AudioRepository;", "(Lcom/bk/trafficcontrol/domain/repository/AudioRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/bk/trafficcontrol/ui/schedule/ScheduleUiState;", "currentPlaylistId", "", "Ljava/lang/Long;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "addSampleTrack", "", "cleanupForUniformMode", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "loadPerDayTracks", "tracks", "", "Lcom/bk/trafficcontrol/domain/model/Track;", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "loadTracks", "loadTracksForMode", "mode", "Lcom/bk/trafficcontrol/ui/schedule/ScheduleMode;", "(Lcom/bk/trafficcontrol/ui/schedule/ScheduleMode;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "loadUniformTracks", "onDaySelected", "day", "", "onModeChange", "newMode", "onTrackToggle", "trackId", "enabled", "", "setPlaylistId", "playlistId", "synchronizeUniformSchedules", "referenceTime", "Ljava/time/LocalTime;", "(JZLjava/time/LocalTime;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toggleTrackForDay", "(JIZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toggleTrackSchedule", "track", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class ScheduleViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.bk.trafficcontrol.domain.repository.AudioRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.bk.trafficcontrol.ui.schedule.ScheduleUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.bk.trafficcontrol.ui.schedule.ScheduleUiState> uiState = null;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Long currentPlaylistId;
    
    @javax.inject.Inject()
    public ScheduleViewModel(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.repository.AudioRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.bk.trafficcontrol.ui.schedule.ScheduleUiState> getUiState() {
        return null;
    }
    
    public final void setPlaylistId(long playlistId) {
    }
    
    private final void loadTracks() {
    }
    
    public final void addSampleTrack() {
    }
    
    public final void onModeChange(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.ui.schedule.ScheduleMode newMode) {
    }
    
    private final java.lang.Object cleanupForUniformMode(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void onDaySelected(int day) {
    }
    
    public final void onTrackToggle(long trackId, boolean enabled) {
    }
    
    private final java.lang.Object toggleTrackForDay(long trackId, int day, boolean enabled, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object loadTracksForMode(com.bk.trafficcontrol.ui.schedule.ScheduleMode mode, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object loadUniformTracks(java.util.List<com.bk.trafficcontrol.domain.model.Track> tracks, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object loadPerDayTracks(java.util.List<com.bk.trafficcontrol.domain.model.Track> tracks, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void toggleTrackSchedule(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Track track) {
    }
    
    private final java.lang.Object synchronizeUniformSchedules(long trackId, boolean enabled, java.time.LocalTime referenceTime, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}