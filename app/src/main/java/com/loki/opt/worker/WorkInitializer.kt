package com.loki.opt.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import javax.inject.Inject

class WorkInitializer @Inject constructor(
    private val context: Context
) {

    val workManager: WorkManager = WorkManager.getInstance(context)

    fun initialize(workName: String) {
        val request = OneTimeWorkRequestBuilder<LockScreenWorker>()
            .build()

        WorkManager.getInstance(context).apply {
            enqueueUniqueWork(
                workName,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                request
            )
        }
    }
}