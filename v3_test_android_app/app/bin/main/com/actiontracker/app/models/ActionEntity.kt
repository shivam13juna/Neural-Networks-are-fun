package com.actiontracker.app.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actions")
data class ActionEntity(
    @PrimaryKey(autoGenerate = true) val actionId: Int = 0,
    val actionName: String,
    val creationTimestamp: Long
)
