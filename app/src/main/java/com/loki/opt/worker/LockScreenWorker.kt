package com.loki.opt.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.loki.opt.services.PolicyManager
import com.loki.opt.util.startLockService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class LockScreenWorker @AssistedInject constructor(
    private val policyManager: PolicyManager,
    @Assisted val context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return if (policyManager.isActive()) {
            context.startLockService()
            Result.success()
        } else {
            Result.failure()
        }
    }
}