package com.loki.opt.worker

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.loki.opt.ForegroundService
import com.loki.opt.MusicManager
import com.loki.opt.PolicyManager
import com.loki.opt.data.database.Schedule
import com.loki.opt.data.datastore.DataStoreStorage
import com.loki.opt.data.repository.OptRepository
import com.loki.opt.util.ext.toSingleDigit
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

@HiltWorker
class LockScreenWorker @AssistedInject constructor(
    private val optRepository: OptRepository,
    private val datastore: DataStoreStorage,
    private val policyManager: PolicyManager,
    private val musicManager: MusicManager,
    @Assisted val context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val schedules = mutableStateOf<List<Schedule>>(emptyList())

    init {
        getSchedules()
    }

    override suspend fun doWork(): Result {

        if (policyManager.isActive()) {

            launchTimeUpdate {

                for (schedule in schedules.value) {

                    val activitySetting = datastore.getActivitySetting().first()

                    if (schedule.isEnabled) {

                        if (
                            activitySetting.isFullScreenNotification &&
                            Settings.canDrawOverlays(context) &&
                            isTimeToShowNotification(it, schedule.offTime)
                        ) {
                            context.startService(Intent(context, ForegroundService::class.java))
                        }

                        if (isTimeToLock(it, schedule.offTime)) {

                            delay(2000L)

                            if (activitySetting.isMusicToStop) {
                                musicManager.stopBackgroundMusic()
                            }

                            policyManager.lockScreen()
                            context.stopService(Intent(context, ForegroundService::class.java))
                        }
                    }
                }
            }
            return Result.success()
        } else {
            return Result.failure()
        }
    }

    private fun getSchedules() {
        CoroutineScope(Dispatchers.IO).launch {
            optRepository.getAllSchedules().collect {
                schedules.value = it
            }
        }
    }

    private suspend fun launchTimeUpdate(time: suspend (Calendar) -> Unit) {
        while (true) {
            val currentTime = Calendar.getInstance()
            time(currentTime)
            delay(10 * 4000L)
        }
    }

    private fun isTimeToLock(currentTime: Calendar, savedTime: String): Boolean {

        val timeArr = savedTime.split(":")
        val hour = timeArr[0].toSingleDigit()
        val minute = timeArr[1].toSingleDigit()
        val time = hour + minute

        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY).toString()
        val currentMinute = currentTime.get(Calendar.MINUTE).toString()
        val currTime = currentHour + currentMinute

        return time == currTime
    }

    private fun isTimeToShowNotification(currentTime: Calendar, savedTime: String): Boolean {

        val timeArr = savedTime.split(":")
        val minute = timeArr[1].toSingleDigit()

        val currentMinute = currentTime.get(Calendar.MINUTE)

        val minuteDifference = minute.toInt() - currentMinute

        return minuteDifference == 5 || minuteDifference == -5
    }
}