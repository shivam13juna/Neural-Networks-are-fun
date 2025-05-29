package com.bk.trafficcontrol.receiver;

@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\u0010\u0010\u000f\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002J8\u0010\u0010\u001a\u00020\n2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u0019\u001a\u00020\u0017H\u0002R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b\u00a8\u0006\u001a"}, d2 = {"Lcom/bk/trafficcontrol/receiver/BootReceiver;", "Landroid/content/BroadcastReceiver;", "()V", "repository", "Lcom/bk/trafficcontrol/domain/repository/AudioRepository;", "getRepository", "()Lcom/bk/trafficcontrol/domain/repository/AudioRepository;", "setRepository", "(Lcom/bk/trafficcontrol/domain/repository/AudioRepository;)V", "onReceive", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "rescheduleAllTasks", "scheduleWork", "workManager", "Landroidx/work/WorkManager;", "scheduleId", "", "trackId", "dayOfWeek", "", "hour", "minute", "app_debug"})
public final class BootReceiver extends android.content.BroadcastReceiver {
    @javax.inject.Inject()
    public com.bk.trafficcontrol.domain.repository.AudioRepository repository;
    
    public BootReceiver() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bk.trafficcontrol.domain.repository.AudioRepository getRepository() {
        return null;
    }
    
    public final void setRepository(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.repository.AudioRepository p0) {
    }
    
    @java.lang.Override()
    public void onReceive(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.content.Intent intent) {
    }
    
    private final void rescheduleAllTasks(android.content.Context context) {
    }
    
    private final void scheduleWork(androidx.work.WorkManager workManager, long scheduleId, long trackId, int dayOfWeek, int hour, int minute) {
    }
}