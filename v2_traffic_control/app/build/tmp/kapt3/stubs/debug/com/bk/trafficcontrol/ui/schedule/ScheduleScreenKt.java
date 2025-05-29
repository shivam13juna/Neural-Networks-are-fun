package com.bk.trafficcontrol.ui.schedule;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\\\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\u001aV\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\b2\u0018\u0010\t\u001a\u0014\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\n2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0007\u001a.\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0007\u001a+\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u00152\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u000b2\b\b\u0002\u0010\u0017\u001a\u00020\u0018H\u0007\u00a2\u0006\u0002\u0010\u0019\u001a.\u0010\u001a\u001a\u00020\u00012\u0006\u0010\u001b\u001a\u00020\u001c2\u0012\u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0007\u001a:\u0010\u001e\u001a\u00020\u00012\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u001c0\u00032\u0018\u0010\t\u001a\u0014\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\n2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0007\u001a\u0010\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0006H\u0002\u00a8\u0006#"}, d2 = {"PerDayScheduleView", "", "days", "", "Lcom/bk/trafficcontrol/ui/schedule/DaySchedule;", "selectedDay", "", "onDaySelected", "Lkotlin/Function1;", "onTrackToggle", "Lkotlin/Function2;", "", "", "modifier", "Landroidx/compose/ui/Modifier;", "ScheduleModeSelector", "mode", "Lcom/bk/trafficcontrol/ui/schedule/ScheduleMode;", "onModeChange", "ScheduleScreen", "navController", "Landroidx/navigation/NavController;", "playlistId", "viewModel", "Lcom/bk/trafficcontrol/ui/schedule/ScheduleViewModel;", "(Landroidx/navigation/NavController;Ljava/lang/Long;Lcom/bk/trafficcontrol/ui/schedule/ScheduleViewModel;)V", "TrackScheduleCard", "trackItem", "Lcom/bk/trafficcontrol/ui/schedule/TrackItem;", "onToggle", "UniformScheduleView", "tracks", "getDayName", "", "dayOfWeek", "app_debug"})
public final class ScheduleScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void ScheduleScreen(@org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController, @org.jetbrains.annotations.Nullable()
    java.lang.Long playlistId, @org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.ui.schedule.ScheduleViewModel viewModel) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void ScheduleModeSelector(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.ui.schedule.ScheduleMode mode, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.bk.trafficcontrol.ui.schedule.ScheduleMode, kotlin.Unit> onModeChange, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void UniformScheduleView(@org.jetbrains.annotations.NotNull()
    java.util.List<com.bk.trafficcontrol.ui.schedule.TrackItem> tracks, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.Long, ? super java.lang.Boolean, kotlin.Unit> onTrackToggle, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void PerDayScheduleView(@org.jetbrains.annotations.NotNull()
    java.util.List<com.bk.trafficcontrol.ui.schedule.DaySchedule> days, int selectedDay, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onDaySelected, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.Long, ? super java.lang.Boolean, kotlin.Unit> onTrackToggle, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void TrackScheduleCard(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.ui.schedule.TrackItem trackItem, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onToggle, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    private static final java.lang.String getDayName(int dayOfWeek) {
        return null;
    }
}