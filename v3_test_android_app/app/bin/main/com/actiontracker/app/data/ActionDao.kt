package com.actiontracker.app.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.actiontracker.app.models.ActionEntity

@Dao
interface ActionDao {
    @Query("SELECT * FROM actions ORDER BY creationTimestamp ASC")
    fun getAllActions(): LiveData<List<ActionEntity>>
    
    @Insert
    suspend fun insertAction(actionEntity: ActionEntity): Long
    
    @Update
    suspend fun updateAction(actionEntity: ActionEntity)
    
    @Query("UPDATE actions SET backgroundColor = :color WHERE actionId = :actionId")
    suspend fun updateActionColor(actionId: Int, color: Int)
    
    @Delete
    suspend fun deleteAction(actionEntity: ActionEntity)
    
    @Query("SELECT * FROM actions WHERE actionId = :actionId")
    suspend fun getActionById(actionId: Int): ActionEntity?
}
