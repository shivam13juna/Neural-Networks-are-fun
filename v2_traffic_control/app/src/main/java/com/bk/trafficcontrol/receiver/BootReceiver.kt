package com.bk.trafficcontrol.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.bk.trafficcontrol.domain.repository.AudioRepository
import com.bk.trafficcontrol.scheduler.ScheduleWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: AudioRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            intent.action == Intent.ACTION_PACKAGE_REPLACED) {
            
            // Reschedule all active schedules
            rescheduleAllTasks(context)
        }
    }

    private fun rescheduleAllTasks(context: Context) {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        
        coroutineScope.launch {
            try {
                repository.getAllActiveSchedules().collect { schedules ->
                    val workManager = WorkManager.getInstance(context)
                    
                    // Cancel all existing work
                    workManager.cancelAllWorkByTag("SCHEDULE_WORK")
                    
                    // Reschedule all active schedules
                    schedules.forEach { schedule ->
                        val track = repository.getTrackById(schedule.trackId)
                        if (track != null) {
                            scheduleWork(workManager, schedule.id, track.id, schedule.dayOfWeek, schedule.hour, schedule.minute)
                        }
                    }
                }
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    private fun scheduleWork(
        workManager: WorkManager,
        scheduleId: Long,
        trackId: Long,
        dayOfWeek: Int,
        hour: Int,
        minute: Int
    ) {
        val now = LocalDateTime.now()
        val scheduledTime = now.with(java.time.DayOfWeek.of(dayOfWeek))
            .withHour(hour)
            .withMinute(minute)
            .withSecond(0)
            .withNano(0)

        // If the scheduled time has passed this week, schedule for next week
        val targetTime = if (scheduledTime.isBefore(now)) {
            scheduledTime.plusWeeks(1)
        } else {
            scheduledTime
        }

        val delayInMillis = targetTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - 
                           System.currentTimeMillis()

        val workData = workDataOf(
            "scheduleId" to scheduleId,
            "trackId" to trackId
        )

        val workRequest = OneTimeWorkRequestBuilder<ScheduleWorker>()
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .setInputData(workData)
            .addTag("SCHEDULE_WORK")
            .build()

        workManager.enqueue(workRequest)
    }
}
