package com.actiontracker.app.models

import androidx.room.Entity

@Entity(tableName = "day_records", primaryKeys = ["date", "actionId"])
data class DayRecordEntity(
    val date: String,    // Store as "YYYY-MM-DD"
    val actionId: Int,
    val count: Int
)
