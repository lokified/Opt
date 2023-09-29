package com.loki.opt.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.loki.opt.PolicyManager
import com.loki.opt.R
import com.loki.opt.data.repository.OptRepository
import com.loki.opt.util.ext.toSingleDigit
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.util.Calendar

@HiltWorker
class LockScreenWorker @AssistedInject constructor(
    private val optRepository: OptRepository,
    private val manager: PolicyManager,
    @Assisted val context: Context,
    @Assisted params: WorkerParameters
): CoroutineWorker(context, params) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return notification()
    }

    override suspend fun doWork(): Result {

        if (manager.isActive()) {

            optRepository.getAllSchedules().collect { schedules ->

                for (schedule in schedules) {
                    launchTimeUpdate {

                        if (isTimeToLock(it, schedule.offTime)
                            && schedule.isEnabled
                        ) {
                            startForegroundService()
                            delay(5000L)
                            manager.lockScreen()

                        }
                    }
                }
            }
            return Result.success()
        }
        else {
            return Result.failure()
        }
    }

    private suspend fun launchTimeUpdate(time: suspend (Calendar) -> Unit) {
        while (true) {
            val currentTime = Calendar.getInstance()
            time(currentTime)
            delay(10 * 2000L)
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

    private suspend fun startForegroundService() {
        setForeground(notification())
    }

    private fun notification() =
        ForegroundInfo(
            NOTIFICATION_ID,
            NotificationCompat.Builder(context, "lockscreen_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("Lock Screen Activated")
                .setAutoCancel(true)
                .build()
        )

    companion object {
        const val NOTIFICATION_ID = 1
    }
}