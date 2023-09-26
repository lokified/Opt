package com.loki.opt.data.repository

import com.loki.opt.data.database.OptDao
import com.loki.opt.data.database.Schedule
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OptRepositoryImpl @Inject constructor(
    private val dao: OptDao
): OptRepository {

    override fun getAllSchedules(): Flow<List<Schedule>> {
        return dao.getALlSchedules()
    }

    override suspend fun saveSchedule(schedule: Schedule) {
        dao.saveSchedule(schedule)
    }

    override suspend fun enableSchedule(isEnabled: Boolean, scheduleId: Int) {
        dao.enableSchedule(isEnabled, scheduleId)
    }

    override suspend fun deleteSchedule(schedule: Schedule) {
        dao.deleteSchedule(schedule)
    }
}