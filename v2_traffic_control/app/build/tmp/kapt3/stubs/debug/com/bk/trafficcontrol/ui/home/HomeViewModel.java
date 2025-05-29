package com.bk.trafficcontrol.ui.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\fJ\u000e\u0010\u0014\u001a\u00020\u0012H\u0082@\u00a2\u0006\u0002\u0010\u0015J\b\u0010\u0016\u001a\u00020\u0012H\u0002J\b\u0010\u0017\u001a\u00020\u0012H\u0002J\u0006\u0010\u0018\u001a\u00020\u0012J\u000e\u0010\u0019\u001a\u00020\u00122\u0006\u0010\u001a\u001a\u00020\u001bR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R&\u0010\b\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f0\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00070\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001c"}, d2 = {"Lcom/bk/trafficcontrol/ui/home/HomeViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/bk/trafficcontrol/domain/repository/AudioRepository;", "(Lcom/bk/trafficcontrol/domain/repository/AudioRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/bk/trafficcontrol/ui/home/HomeUiState;", "defaultScheduleSlots", "", "Lkotlin/Triple;", "", "", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "createCustomPlaylist", "", "name", "createDefaultPlaylists", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "initializeDefaultPlaylists", "loadPlaylists", "toggleMasterEnabled", "togglePlaylistEnabled", "playlist", "Lcom/bk/trafficcontrol/domain/model/Playlist;", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class HomeViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.bk.trafficcontrol.domain.repository.AudioRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<kotlin.Triple<java.lang.Integer, java.lang.Integer, java.lang.String>> defaultScheduleSlots = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.bk.trafficcontrol.ui.home.HomeUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.bk.trafficcontrol.ui.home.HomeUiState> uiState = null;
    
    @javax.inject.Inject()
    public HomeViewModel(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.repository.AudioRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.bk.trafficcontrol.ui.home.HomeUiState> getUiState() {
        return null;
    }
    
    private final void loadPlaylists() {
    }
    
    private final void initializeDefaultPlaylists() {
    }
    
    private final java.lang.Object createDefaultPlaylists(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void togglePlaylistEnabled(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.Playlist playlist) {
    }
    
    /**
     * Toggles all playlists on or off based on the master switch state.
     */
    public final void toggleMasterEnabled() {
    }
    
    public final void createCustomPlaylist(@org.jetbrains.annotations.NotNull()
    java.lang.String name) {
    }
}