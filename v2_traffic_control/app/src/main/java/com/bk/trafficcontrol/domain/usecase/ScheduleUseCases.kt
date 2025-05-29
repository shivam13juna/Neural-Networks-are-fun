package com.bk.trafficcontrol.domain.usecase

import com.bk.trafficcontrol.domain.model.Schedule
import com.bk.trafficcontrol.domain.repository.AudioRepository
import javax.inject.Inject

class ScheduleTrackUseCase @Inject constructor(
    private val repository: AudioRepository
) {
    suspend operator fun invoke(schedule: Schedule): Long {
        return repository.insertSchedule(schedule)
    }
}

class UpdateScheduleUseCase @Inject constructor(
    private val repository: AudioRepository
) {
    suspend operator fun invoke(schedule: Schedule) {
        repository.updateSchedule(schedule)
    }
}

class DeleteScheduleUseCase @Inject constructor(
    private val repository: AudioRepository
) {
    suspend operator fun invoke(schedule: Schedule) {
        repository.deleteSchedule(schedule)
    }
}

class GetActiveSchedulesUseCase @Inject constructor(
    private val repository: AudioRepository
) {
    operator fun invoke() = repository.getAllActiveSchedules()
}
