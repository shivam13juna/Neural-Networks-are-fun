package com.bk.trafficcontrol.data.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u0004H\u0007\u00a8\u0006\t"}, d2 = {"Lcom/bk/trafficcontrol/data/db/Converters;", "", "()V", "fromPlaylistType", "", "type", "Lcom/bk/trafficcontrol/domain/model/PlaylistType;", "toPlaylistType", "name", "app_debug"})
public final class Converters {
    
    public Converters() {
        super();
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String fromPlaylistType(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.domain.model.PlaylistType type) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final com.bk.trafficcontrol.domain.model.PlaylistType toPlaylistType(@org.jetbrains.annotations.NotNull()
    java.lang.String name) {
        return null;
    }
}