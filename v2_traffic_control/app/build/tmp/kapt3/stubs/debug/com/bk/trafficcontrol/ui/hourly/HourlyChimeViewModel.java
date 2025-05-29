package com.bk.trafficcontrol.ui.hourly;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\b\u0010\u0010\u001a\u00020\u0011H\u0002J\u000e\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\rJ\u000e\u0010\u0014\u001a\u00020\u00112\u0006\u0010\u0015\u001a\u00020\rJ\u000e\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\rJ\u0016\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0015\u001a\u00020\r2\u0006\u0010\u0018\u001a\u00020\u0019R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u001a"}, d2 = {"Lcom/bk/trafficcontrol/ui/hourly/HourlyChimeViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/bk/trafficcontrol/domain/repository/AudioRepository;", "(Lcom/bk/trafficcontrol/domain/repository/AudioRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/bk/trafficcontrol/ui/hourly/HourlyChimeUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "extractHourFromTrackTitle", "", "title", "", "loadHourlyChimeData", "", "selectDay", "day", "selectHourSlot", "hour", "toggleDayFilter", "toggleHourSlot", "enabled", "", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class HourlyChimeViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.bk.trafficcontrol.domain.repository.AudioRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.bk.trafficcontrol.ui.hourly.HourlyChimeUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.bk.trafficcontrol.ui.hourly.HourlyChimeUiState> uiState = null;
    
    @javax.inject.Inject()
    public HourlyChimeViewModel(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.repository.AudioRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.bk.trafficcontrol.ui.hourly.HourlyChimeUiState> getUiState() {
        return null;
    }
    
    private final void loadHourlyChimeData() {
    }
    
    public final void selectDay(int day) {
    }
    
    public final void selectHourSlot(int hour) {
    }
    
    public final void toggleDayFilter(int day) {
    }
    
    public final void toggleHourSlot(int hour, boolean enabled) {
    }
    
    private final int extractHourFromTrackTitle(java.lang.String title) {
        return 0;
    }
}