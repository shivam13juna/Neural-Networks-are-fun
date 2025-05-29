package com.bk.trafficcontrol.util;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J(\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00042\b\b\u0002\u0010\b\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\nJ\u001e\u0010\u000b\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\u0004H\u0086@\u00a2\u0006\u0002\u0010\r\u00a8\u0006\u000e"}, d2 = {"Lcom/bk/trafficcontrol/util/AudioClipper;", "", "()V", "clipToMaxDuration", "Landroid/net/Uri;", "context", "Landroid/content/Context;", "sourceUri", "maxDurationSec", "", "(Landroid/content/Context;Landroid/net/Uri;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAudioDuration", "uri", "(Landroid/content/Context;Landroid/net/Uri;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class AudioClipper {
    
    @javax.inject.Inject()
    public AudioClipper() {
        super();
    }
    
    /**
     * Clips audio file to maximum 60 seconds with fade out in last 2 seconds
     * @param context Application context
     * @param sourceUri URI of the source audio file
     * @param maxDurationSec Maximum duration in seconds (default 60)
     * @return URI of the clipped file or original URI if no clipping needed
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clipToMaxDuration(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri sourceUri, int maxDurationSec, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super android.net.Uri> $completion) {
        return null;
    }
    
    /**
     * Get audio duration in seconds
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAudioDuration(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
}