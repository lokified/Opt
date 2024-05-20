package com.loki.opt.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.loki.opt.R

class NotificationForegroundService : Service() {

    private lateinit var fullScreenNotificationView: FullScreenNotificationView

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet Implemented")
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyForeground()
        } else {
            startForeground(4, Notification())
        }

        fullScreenNotificationView = FullScreenNotificationView(applicationContext)
        fullScreenNotificationView.open()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        fullScreenNotificationView.removeOverLay()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyForeground() {

        val channel = NotificationChannel(
            "Full_Lockscreen_channel",
            "Opt Full LockScreen",
            NotificationManager.IMPORTANCE_MIN
        )

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, "Full_Lockscreen_channel")
            .setContentTitle("Opt Lock Notification")
            .setSmallIcon(R.mipmap.opt_icon_launcher)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setAutoCancel(true)
            .build()

        startForeground(3, notification)
    }
}