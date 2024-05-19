package com.loki.opt.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.loki.opt.MainActivity
import com.loki.opt.R
import com.loki.opt.data.database.Schedule
import com.loki.opt.data.datastore.DataStoreStorage
import com.loki.opt.data.repository.OptRepository
import com.loki.opt.util.startNotifService
import com.loki.opt.util.stopLockService
import com.loki.opt.util.stopNotifService
import com.loki.opt.util.toSingleDigit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class LockForegroundService: Service() {

    @Inject
    lateinit var datastore: DataStoreStorage
    @Inject
    lateinit var optRepository: OptRepository
    @Inject
    lateinit var musicManager: MusicManager
    @Inject
    lateinit var policyManager: PolicyManager

    private val schedules = mutableStateOf<List<Schedule>>(emptyList())

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val command = intent?.getStringExtra(INTENT_COMMAND) ?: ""

        return when(command) {
            INTENT_COMMAND_OPEN -> {
                Intent(this, MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(it)
                }
                START_STICKY
            }

            INTENT_COMMAND_DISABLE -> {
                CoroutineScope(Dispatchers.IO).launch {
                    for (schedule in schedules.value) {
                        if (schedule.isEnabled) {
                            optRepository.enableSchedule(false, schedule.id)
                        }
                    }
                }
                stopLockService()
                START_NOT_STICKY
            }

            else -> START_STICKY
        }
    }

    override fun onCreate() {
        super.onCreate()

        getSchedules()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyForeground()
        } else {
            startForeground(1, Notification())
        }

        startLock(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyForeground() {

        val openIntent = Intent(this, LockForegroundService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_OPEN)
        }

        val disableIntent = Intent(this, LockForegroundService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_DISABLE)
        }

        val openPendingIntent = PendingIntent.getService(this, INTENT_CODE_OPEN, openIntent, PendingIntent.FLAG_IMMUTABLE)
        val disablePendingIntent = PendingIntent.getService(this, INTENT_CODE_DISABLE, disableIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, "lockscreen_channel")
            .setContentTitle("Opt Lock Screen Activated")
            .setSmallIcon(R.mipmap.ic_opt_launcher)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .addAction(
                NotificationCompat.Action(
                    0,
                    "Open",
                    openPendingIntent
                )
            ).addAction(
                NotificationCompat.Action(
                    0,
                    "Disable",
                    disablePendingIntent
                )
            )
            .setAutoCancel(true)
            .build()

        startForeground(2, notification)
    }

    private fun startLock(context: Context) {

        CoroutineScope(Dispatchers.IO).launch {
            val activitySetting = datastore.getActivitySetting().first()

            launchTimeUpdate {

                for (schedule in schedules.value) {
                    if (schedule.isEnabled) {
                        if (
                            activitySetting.isFullScreenNotification &&
                            Settings.canDrawOverlays(context) &&
                            isTimeToShowNotification(it, schedule.offTime)
                        ) {
                            context.startNotifService()
                        }

                        if (isTimeToLock(it, schedule.offTime)) {

                            delay(2000L)

                            if (activitySetting.isMusicToStop) {
                                musicManager.stopBackgroundMusic()
                            }

                            policyManager.lockScreen()
                            context.stopNotifService()
                        }
                    }
                }
            }
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
            delay(10 * 6000L)
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
        val hour = timeArr[0].toSingleDigit()
        val minute = timeArr[1].toSingleDigit()

        val currentMinute = currentTime.get(Calendar.MINUTE)
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY).toString()
        val minuteDifference = minute.toInt() - currentMinute

        return minuteDifference == 5 && (hour == currentHour || hour.toInt() - 1 == currentHour.toInt())
    }

    companion object {
        const val INTENT_COMMAND = "intent_command"
        const val INTENT_COMMAND_OPEN = "intent_open"
        const val INTENT_COMMAND_DISABLE = "intent_disable"
        const val INTENT_CODE_OPEN = 0
        const val INTENT_CODE_DISABLE = 1
    }
}