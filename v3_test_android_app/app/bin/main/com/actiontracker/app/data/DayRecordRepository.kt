package com.actiontracker.app.data

import androidx.lifecycle.LiveData
import com.actiontracker.app.models.DayRecordEntity

class DayRecordRepository(private val dayRecordDao: DayRecordDao) {
    
    fun getDayRecordsForDate(date: String): LiveData<List<DayRecordEntity>> {
        return dayRecordDao.getDayRecordsForDate(date)
    }
    
    suspend fun incrementCount(date: String, actionId: Int) {
        dayRecordDao.incrementCount(date, actionId)
    }
    
    suspend fun decrementCount(date: String, actionId: Int) {
        dayRecordDao.decrementCount(date, actionId)
    }
    
    suspend fun getCountForDateAndAction(date: String, actionId: Int): Int {
        return dayRecordDao.getDayRecordForDateAndAction(date, actionId)?.count ?: 0
    }
    
    suspend fun deleteAllRecordsForAction(actionId: Int) {
        dayRecordDao.deleteAllRecordsForAction(actionId)
    }
}
