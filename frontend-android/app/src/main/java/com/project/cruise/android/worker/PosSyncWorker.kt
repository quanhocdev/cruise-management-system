package com.project.cruise.android.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.project.cruise.android.BuildConfig
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.local.CruiseDatabase
import com.project.cruise.android.data.local.pos.PosSyncStatus
import com.project.cruise.android.data.network.QrScanRequest
import com.project.cruise.android.data.network.RetrofitClient
import retrofit2.HttpException

class PosSyncWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val dao = CruiseDatabase.getInstance(applicationContext).posTransactionDao()
        val posApi = RetrofitClient.createPosApiService(TokenManager(applicationContext))
        val waiting = dao.findWaitingForSync()
        if (waiting.isEmpty()) return Result.success()

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
                // Gọi thẳng endpoint /api/finance/scan với DTO chứa bookingCode (lấy từ scannedValue)
                val response = posApi.scanQrCode(
                    QrScanRequest(bookingCode = transaction.scannedValue)
                )

                val isSuccess = response.success
                dao.update(attempt.copy(
                    status = if (isSuccess) PosSyncStatus.SYNCED.name else PosSyncStatus.FAILED.name,
                    updatedAt = System.currentTimeMillis(),
                    lastError = if (isSuccess) null else (response.message ?: "Server từ chối đồng bộ")
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