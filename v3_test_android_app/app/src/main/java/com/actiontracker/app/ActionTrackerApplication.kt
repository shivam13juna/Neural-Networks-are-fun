package com.actiontracker.app

import android.app.Application
import com.actiontracker.app.data.AppDatabase

class ActionTrackerApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    
    override fun onCreate() {
        super.onCreate()
        // Application initialization
    }
}
