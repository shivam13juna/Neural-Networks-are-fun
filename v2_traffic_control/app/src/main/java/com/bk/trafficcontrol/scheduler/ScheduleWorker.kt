package com.bk.trafficcontrol.scheduler

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bk.trafficcontrol.domain.repository.AudioRepository
import com.bk.trafficcontrol.service.AudioPlayerService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: AudioRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val trackId = inputData.getLong("trackId", -1)
            val scheduleId = inputData.getLong("scheduleId", -1)
            
            if (trackId == -1L || scheduleId == -1L) {
                return Result.failure()
            }

            // Get track and schedule details
            val track = repository.getTrackById(trackId)
            val schedule = repository.getScheduleById(scheduleId)

            if (track == null || schedule == null || !schedule.enabled) {
                return Result.failure()
            }

            // Start audio playback service
            val intent = Intent(applicationContext, AudioPlayerService::class.java).apply {
                action = AudioPlayerService.ACTION_PLAY
                putExtra(AudioPlayerService.EXTRA_AUDIO_URI, track.uri)
                putExtra(AudioPlayerService.EXTRA_TRACK_TITLE, track.title)
            }
            
            applicationContext.startForegroundService(intent)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
