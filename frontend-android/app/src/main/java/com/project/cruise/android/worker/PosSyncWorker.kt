package com.project.cruise.android.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.project.cruise.android.BuildConfig
import com.project.cruise.android.data.local.CruiseDatabase
import com.project.cruise.android.data.local.pos.PosSyncStatus
import com.project.cruise.android.data.network.PosSyncRequest
import com.project.cruise.android.data.network.RetrofitClient
import retrofit2.HttpException
import java.time.Instant

class PosSyncWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val dao = CruiseDatabase.getInstance(applicationContext).posTransactionDao()
        val waiting = dao.findWaitingForSync()
        if (waiting.isEmpty()) return Result.success()

        if (BuildConfig.POS_API_KEY.isBlank()) {
            waiting.forEach { transaction ->
                dao.update(transaction.copy(
                    status = PosSyncStatus.FAILED.name,
                    attemptCount = transaction.attemptCount + 1,
                    updatedAt = System.currentTimeMillis(),
                    lastError = "Thiết bị POS chưa được cấp khóa đồng bộ"
                ))
            }
            return Result.success()
        }

        var shouldRetry = false
        waiting.forEach { transaction ->
            val attempt = transaction.copy(
                status = PosSyncStatus.SYNCING.name,
                attemptCount = transaction.attemptCount + 1,
                updatedAt = System.currentTimeMillis(),
                lastError = null
            )
            dao.update(attempt)
            try {
                val response = RetrofitClient.posApiService.sync(
                    terminalCode = BuildConfig.POS_TERMINAL_CODE,
                    posKey = BuildConfig.POS_API_KEY,
                    request = PosSyncRequest(
                        localId = attempt.localId,
                        scanType = attempt.scanType,
                        scannedValue = attempt.scannedValue,
                        createdAt = Instant.ofEpochMilli(attempt.createdAt).toString()
                    )
                )
                dao.update(attempt.copy(
                    status = if (response.status == "SYNCED") PosSyncStatus.SYNCED.name else PosSyncStatus.FAILED.name,
                    updatedAt = System.currentTimeMillis(),
                    lastError = if (response.status == "SYNCED") null else "Server từ chối đồng bộ"
                ))
            } catch (error: HttpException) {
                val permanent = error.code() == 401 || error.code() == 403 || error.code() == 400
                dao.update(attempt.copy(
                    status = if (permanent) PosSyncStatus.FAILED.name else PosSyncStatus.PENDING_SYNC.name,
                    updatedAt = System.currentTimeMillis(),
                    lastError = "Đồng bộ lỗi HTTP ${error.code()}"
                ))
                shouldRetry = shouldRetry || !permanent
            } catch (_: Exception) {
                dao.update(attempt.copy(
                    status = PosSyncStatus.PENDING_SYNC.name,
                    updatedAt = System.currentTimeMillis(),
                    lastError = "Mất kết nối tới máy chủ"
                ))
                shouldRetry = true
            }
        }
        return if (shouldRetry) Result.retry() else Result.success()
    }
}
