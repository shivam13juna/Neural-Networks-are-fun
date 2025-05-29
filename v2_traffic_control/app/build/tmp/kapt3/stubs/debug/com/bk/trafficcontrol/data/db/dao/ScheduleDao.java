package com.bk.trafficcontrol.data.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0006\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\fH\'J\u0018\u0010\u000e\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000f\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u001c\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\f2\u0006\u0010\b\u001a\u00020\tH\'J\u0016\u0010\u0011\u001a\u00020\t2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u0013"}, d2 = {"Lcom/bk/trafficcontrol/data/db/dao/ScheduleDao;", "", "deleteSchedule", "", "schedule", "Lcom/bk/trafficcontrol/data/db/entity/ScheduleEntity;", "(Lcom/bk/trafficcontrol/data/db/entity/ScheduleEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteSchedulesByTrack", "trackId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllActiveSchedules", "Lkotlinx/coroutines/flow/Flow;", "", "getScheduleById", "id", "getSchedulesByTrack", "insertSchedule", "updateSchedule", "app_debug"})
@androidx.room.Dao()
public abstract interface ScheduleDao {
    
    @androidx.room.Query(value = "SELECT * FROM schedules WHERE trackId = :trackId ORDER BY dayOfWeek, hour, minute")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.bk.trafficcontrol.data.db.entity.ScheduleEntity>> getSchedulesByTrack(long trackId);
    
    @androidx.room.Query(value = "SELECT * FROM schedules WHERE enabled = 1 ORDER BY dayOfWeek, hour, minute")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.bk.trafficcontrol.data.db.entity.ScheduleEntity>> getAllActiveSchedules();
    
    @androidx.room.Query(value = "SELECT * FROM schedules WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getScheduleById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.bk.trafficcontrol.data.db.entity.ScheduleEntity> $completion);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertSchedule(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.data.db.entity.ScheduleEntity schedule, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateSchedule(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.data.db.entity.ScheduleEntity schedule, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteSchedule(@org.jetbrains.annotations.NotNull()
    com.bk.trafficcontrol.data.db.entity.ScheduleEntity schedule, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM schedules WHERE trackId = :trackId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteSchedulesByTrack(long trackId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}