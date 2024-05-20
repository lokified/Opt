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