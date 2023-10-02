package com.loki.opt

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.loki.opt.data.datastore.ActivitySetting
import com.loki.opt.data.datastore.DataStoreStorage
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class OptApp: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    @Inject
    lateinit var dataStoreStorage: DataStoreStorage

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()


    override fun onCreate() {
        super.onCreate()

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "lockscreen_channel",
                "LockScreen",
                NotificationManager.IMPORTANCE_HIGH
            )

             val notificationManager = getSystemService(NotificationManager::class.java)
             notificationManager.createNotificationChannel(channel)
         }

        //init settings
        CoroutineScope(Dispatchers.IO).launch {
            val isActivitySettingEmpty = dataStoreStorage.getActivitySetting().first()

            if (isActivitySettingEmpty == null) {

                dataStoreStorage.setActivitySetting(
                    ActivitySetting(
                        isMusicToStop = false
                    )
                )
            }
        }
    }
}