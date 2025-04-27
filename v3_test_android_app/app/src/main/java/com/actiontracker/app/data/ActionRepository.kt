package com.actiontracker.app.data

import androidx.lifecycle.LiveData
import com.actiontracker.app.models.ActionEntity

class ActionRepository(private val actionDao: ActionDao) {
    
    val allActions: LiveData<List<ActionEntity>> = actionDao.getAllActions()
    
    suspend fun insertAction(actionName: String, backgroundColor: Int = android.graphics.Color.WHITE): Long {
        val action = ActionEntity(
            actionName = actionName,
            creationTimestamp = System.currentTimeMillis(),
            backgroundColor = backgroundColor
        )
        return actionDao.insertAction(action)
    }
    
    suspend fun updateAction(action: ActionEntity) {
        actionDao.updateAction(action)
    }
    
    suspend fun updateActionColor(actionId: Int, color: Int) {
        actionDao.updateActionColor(actionId, color)
    }
    
    suspend fun deleteAction(action: ActionEntity) {
        actionDao.deleteAction(action)
    }
    
    suspend fun getActionById(id: Int): ActionEntity? {
        return actionDao.getActionById(id)
    }
}
