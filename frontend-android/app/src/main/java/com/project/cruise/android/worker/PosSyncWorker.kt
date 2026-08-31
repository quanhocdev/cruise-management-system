package com.project.cruise.android.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.project.cruise.android.BuildConfig
import com.project.cruise.android.data.local.CruiseDatabase
import com.project.cruise.android.data.local.pos.PosSyncStatus

class PosSyncWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val dao = CruiseDatabase.getInstance(applicationContext).posTransactionDao()
        val waiting = dao.findWaitingForSync()
        if (waiting.isEmpty()) return Result.success()

        val configurationError = if (BuildConfig.POS_API_KEY.isBlank()) {
            "Thiết bị POS chưa được cấp khóa đồng bộ"
        } else {
            "API đồng bộ POS chưa được cấu hình"
        }

        waiting.forEach { transaction ->
            dao.update(
                transaction.copy(
                    status = PosSyncStatus.PENDING_SYNC.name,
                    attemptCount = transaction.attemptCount + 1,
                    updatedAt = System.currentTimeMillis(),
                    lastError = configurationError
                )
            )
        }
        return Result.retry()
    }
}
