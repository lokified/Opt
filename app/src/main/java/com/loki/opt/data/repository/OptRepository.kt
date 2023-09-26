package com.loki.opt.data.repository

import com.loki.opt.data.database.Schedule
import kotlinx.coroutines.flow.Flow

interface OptRepository {

    fun getAllSchedules(): Flow<List<Schedule>>

    suspend fun saveSchedule(schedule: Schedule)

    suspend fun enableSchedule(isEnabled: Boolean, scheduleId: Int)

    suspend fun deleteSchedule(schedule: Schedule)
}