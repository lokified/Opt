package com.loki.opt.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSchedule(schedule: Schedule)

    @Query("SELECT * FROM schedule")
    fun getALlSchedules(): Flow<List<Schedule>>

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Query("UPDATE schedule SET isEnabled = :isEnabled WHERE id = :scheduleId")
    suspend fun enableSchedule(isEnabled: Boolean, scheduleId: Int)
}