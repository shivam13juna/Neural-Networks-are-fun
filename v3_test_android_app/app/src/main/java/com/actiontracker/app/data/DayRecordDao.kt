package com.actiontracker.app.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.actiontracker.app.models.DayRecordEntity

@Dao
interface DayRecordDao {
    @Query("SELECT * FROM day_records WHERE date = :date")
    fun getDayRecordsForDate(date: String): LiveData<List<DayRecordEntity>>
    
    @Query("SELECT * FROM day_records WHERE date = :date AND actionId = :actionId")
    suspend fun getDayRecordForDateAndAction(date: String, actionId: Int): DayRecordEntity?
    
    @Query("SELECT * FROM day_records WHERE date BETWEEN :start AND :end AND actionId IN(:actionIds)")
    fun getDayRecordsForRange(start: String, end: String, actionIds: List<Int>): LiveData<List<DayRecordEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDayRecord(dayRecordEntity: DayRecordEntity)
    
    @Query("DELETE FROM day_records WHERE actionId = :actionId")
    suspend fun deleteAllRecordsForAction(actionId: Int)
    
    @Transaction
    suspend fun incrementCount(date: String, actionId: Int) {
        val record = getDayRecordForDateAndAction(date, actionId)
        val newCount = (record?.count ?: 0) + 1
        insertOrUpdateDayRecord(DayRecordEntity(date, actionId, newCount))
    }
    
    @Transaction
    suspend fun decrementCount(date: String, actionId: Int) {
        val record = getDayRecordForDateAndAction(date, actionId)
        val currentCount = record?.count ?: 0
        if (currentCount > 0) {
            insertOrUpdateDayRecord(DayRecordEntity(date, actionId, currentCount - 1))
        }
    }
}
