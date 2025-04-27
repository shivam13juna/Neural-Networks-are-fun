package com.actiontracker.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import android.graphics.Color
import com.actiontracker.app.data.ActionRepository
import com.actiontracker.app.data.DayRecordRepository
import com.actiontracker.app.models.ActionEntity
import com.actiontracker.app.models.DayRecordEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ActionTrackerViewModel(
    private val actionRepository: ActionRepository,
    private val dayRecordRepository: DayRecordRepository
) : ViewModel() {
    
    companion object {
        private val DATE_FORMATTER = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        private val ISO_FORMATTER = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        class Factory(
            private val actionRepository: ActionRepository, 
            private val dayRecordRepository: DayRecordRepository
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ActionTrackerViewModel(actionRepository, dayRecordRepository) as T
            }
        }
    }

    private val _currentDate = MutableLiveData(Calendar.getInstance())
    val currentDate: LiveData<Calendar> = _currentDate
    
    val currentDateFormatted: LiveData<String> = currentDate.map { date: Calendar ->
        DATE_FORMATTER.format(date.time)
    }
    
    val currentDateString: LiveData<String> = currentDate.map { date: Calendar ->
        ISO_FORMATTER.format(date.time) // Format: YYYY-MM-DD
    }
    
    // Trigger to force refresh the actions list
    private val _refreshTrigger = MutableLiveData(0)
    
    // Combined LiveData that refreshes when either the actual data or the trigger changes
    val allActions = _refreshTrigger.switchMap { actionRepository.allActions }
    
    val dayRecords: LiveData<List<DayRecordEntity>> = currentDateString.switchMap { date: String ->
        dayRecordRepository.getDayRecordsForDate(date)
    }
    
    fun setDate(date: Calendar) {
        _currentDate.value = date
    }
    
    fun previousDay() {
        _currentDate.value?.let { currentDate ->
            val newCalendar = currentDate.clone() as Calendar
            newCalendar.add(Calendar.DAY_OF_MONTH, -1)
            _currentDate.value = newCalendar
        }
    }
    
    fun nextDay() {
        _currentDate.value?.let { currentDate ->
            val newCalendar = currentDate.clone() as Calendar
            newCalendar.add(Calendar.DAY_OF_MONTH, 1)
            _currentDate.value = newCalendar
        }
    }
    
    fun addAction(actionName: String, color: Int = Color.WHITE) {
        if (actionName.isBlank()) return
        
        viewModelScope.launch {
            actionRepository.insertAction(actionName, color)
        }
    }
    
    fun incrementCount(actionId: Int) {
        viewModelScope.launch {
            currentDateString.value?.let { date ->
                dayRecordRepository.incrementCount(date, actionId)
            }
        }
    }
    
    fun decrementCount(actionId: Int) {
        viewModelScope.launch {
            currentDateString.value?.let { date ->
                dayRecordRepository.decrementCount(date, actionId)
            }
        }
    }
    
    fun getActionWithCount(action: ActionEntity, dayRecords: List<DayRecordEntity>): Pair<ActionEntity, Int> {
        val record = dayRecords.find { it.actionId == action.actionId }
        return Pair(action, record?.count ?: 0)
    }
    
    fun deleteAction(action: ActionEntity) {
        viewModelScope.launch {
            // First, delete all day records associated with this action
            dayRecordRepository.deleteAllRecordsForAction(action.actionId)
            
            // Then delete the action itself
            actionRepository.deleteAction(action)
        }
    }
    
    fun updateActionColor(action: ActionEntity, color: Int) {
        viewModelScope.launch {
            // Update using the dedicated color update method
            actionRepository.updateActionColor(action.actionId, color)
            
            // For additional safety, also update the full entity
            val updatedAction = action.copy(backgroundColor = color)
            actionRepository.updateAction(updatedAction)
            
            // Force a UI refresh by incrementing the refresh trigger
            _refreshTrigger.postValue((_refreshTrigger.value ?: 0) + 1)
        }
    }
}
