
package com.project.cruise.android.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.convenience.ProductUsageRequest
import com.project.cruise.android.data.local.CruiseDatabase
import com.project.cruise.android.data.network.core.RetrofitClient
import com.project.cruise.android.data.repository.pos.PosCommandErrors
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class PosCommandSyncWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val dao = CruiseDatabase
            .getInstance(applicationContext)
            .posCommandDao()

        val api = RetrofitClient.createConvenienceApiService(
            TokenManager(applicationContext)
        )

        val gson = Gson()
        val now = System.currentTimeMillis()

        // Chỉ khôi phục các lệnh SYNCING đã bị treo quá 15 phút.
        dao.recoverStaleCommands(
            staleBefore = now - STALE_SYNC_TIMEOUT
        )

        val waitingCommands = dao.findWaitingForSync()

        if (waitingCommands.isEmpty()) {
            return Result.success()
        }

        var shouldRetry = false

        for (command in waitingCommands) {
            if (isStopped) {
                return Result.retry()
            }

            // Atomic claim: tránh hai worker cùng gửi một lệnh.
            val claimed = dao.claimForSync(command.localId)

            if (claimed == 0) {
                continue
            }

            try {
                val request = gson.fromJson(
                    command.payloadJson,
                    ProductUsageRequest::class.java
                )

                requireNotNull(request) {
                    "Không thể đọc dữ liệu lệnh mua sản phẩm."
                }

                require(request.clientRequestId == command.clientRequestId) {
                    "Mã giao dịch không khớp với dữ liệu đã lưu."
                }

                api.createProductUsage(request)

                dao.markConfirmed(command.localId)

            } catch (error: CancellationException) {
                // Không để lệnh đang xử lý bị đánh dấu FAILED khi Worker bị dừng.
                withContext(NonCancellable) {
                    dao.markPending(
                        localId = command.localId,
                        error = "Đồng bộ bị gián đoạn, sẽ thử lại."
                    )
                }
                throw error

            } catch (error: HttpException) {
                val permanent = error.code() in setOf(
                    400,
                    403,
                    404,
                    422
                )

                val serverMessage = runCatching {
                    error.response()
                        ?.errorBody()
                        ?.string()
                        ?.let { PosCommandErrors.message(it) }
                }.getOrNull()

                if (permanent) {
                    dao.markFailed(
                        localId = command.localId,
                        error = serverMessage
                            ?: "Máy chủ từ chối giao dịch. HTTP ${error.code()}"
                    )
                } else {
                    dao.markPending(
                        localId = command.localId,
                        error = serverMessage
                            ?: "Lỗi HTTP ${error.code()}, sẽ thử đồng bộ lại."
                    )
                    shouldRetry = true
                }

            } catch (error: IOException) {
                dao.markPending(
                    localId = command.localId,
                    error = "Mất kết nối tới máy chủ."
                )
                shouldRetry = true

            } catch (error: Exception) {
                dao.markFailed(
                    localId = command.localId,
                    error = error.message
                        ?: "Dữ liệu lệnh không hợp lệ."
                )
            }
        }

        return if (shouldRetry) {
            Result.retry()
        } else {
            Result.success()
        }
    }

    companion object {
        private const val WORK_NAME = "pos-command-sync"
        private const val STALE_SYNC_TIMEOUT = 15 * 60 * 1000L

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request =
                OneTimeWorkRequestBuilder<PosCommandSyncWorker>()
                    .setConstraints(constraints)
                    .build()

            WorkManager.getInstance(context.applicationContext)
                .enqueueUniqueWork(
                    WORK_NAME,
                    ExistingWorkPolicy.APPEND_OR_REPLACE,
                    request
                )
        }
    }
}
