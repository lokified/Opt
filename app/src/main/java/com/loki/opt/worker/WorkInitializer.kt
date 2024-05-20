package com.loki.opt.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WorkInitializer @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val workManager: WorkManager = WorkManager.getInstance(context)

    fun initialize(workName: String) {
        val request = OneTimeWorkRequestBuilder<LockScreenWorker>()
            .build()

        workManager.apply {
            enqueueUniqueWork(
                workName,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                request
            )
        }
    }

    fun cancelWork(workName: String) {
        workManager.cancelUniqueWork(workName)
    }
}