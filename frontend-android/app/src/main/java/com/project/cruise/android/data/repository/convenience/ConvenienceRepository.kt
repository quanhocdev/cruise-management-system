
package com.project.cruise.android.data.repository.convenience

import android.content.Context
import com.google.gson.Gson
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.convenience.ProductTourResponse
import com.project.cruise.android.data.dto.convenience.ProductUsageRequest
import com.project.cruise.android.data.dto.convenience.ProductUsageResponse
import com.project.cruise.android.data.dto.convenience.ServiceTourResponse
import com.project.cruise.android.data.dto.convenience.ServiceUsageRequest
import com.project.cruise.android.data.dto.convenience.ServiceUsageResponse
import com.project.cruise.android.data.dto.nfc.NfcResolveRequest
import com.project.cruise.android.data.dto.nfc.NfcResolveResponse
import com.project.cruise.android.data.local.CruiseDatabase
import com.project.cruise.android.data.local.pos.PosCommandEntity
import com.project.cruise.android.data.local.pos.PosCommandStatus
import com.project.cruise.android.data.network.core.RetrofitClient
import com.project.cruise.android.data.repository.pos.OnlinePosCommands
import com.project.cruise.android.data.repository.pos.PosCommandErrors
import com.project.cruise.android.worker.PosCommandSyncWorker
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.util.UUID

sealed interface ProductUsageSubmissionResult {
    data class Confirmed(
        val response: ProductUsageResponse
    ) : ProductUsageSubmissionResult

    data object Queued : ProductUsageSubmissionResult
}

class ConvenienceRepository(context: Context) {

    private val appContext = context.applicationContext

    private val commands = OnlinePosCommands(appContext)

    private val commandDao =
        CruiseDatabase.getInstance(appContext).posCommandDao()

    private val gson = Gson()

    private val apiService =
        RetrofitClient.createConvenienceApiService(
            TokenManager(appContext)
        )

    suspend fun resolveNfc(
        nfcCardUid: String
    ): NfcResolveResponse {
        return apiService.resolveConvenienceNfc(
            NfcResolveRequest(nfcCardUid = nfcCardUid)
        )
    }

    suspend fun getProductTours(
        tourId: String
    ): List<ProductTourResponse> {
        return apiService.getConvenienceProductTours(tourId)
    }

    suspend fun getServiceTours(
        tourId: String
    ): List<ServiceTourResponse> {
        return apiService.getConvenienceServiceTours(tourId)
    }

    /**
     * PRODUCT_USAGE:
     * 1. Generate a unique request ID.
     * 2. Persist the complete request in Room.
     * 3. Attempt immediate synchronization.
     * 4. Keep the same request ID and payload for retries.
     */
    suspend fun createProductUsage(
        nfcCardUid: String,
        productTourId: String,
        quantity: Int
    ): ProductUsageSubmissionResult {

        require(nfcCardUid.isNotBlank()) {
            "Mã NFC không hợp lệ."
        }

        require(productTourId.isNotBlank()) {
            "Sản phẩm không hợp lệ."
        }

        require(quantity > 0) {
            "Số lượng phải lớn hơn 0."
        }

        val requestId = UUID.randomUUID().toString()
        val localId = UUID.randomUUID().toString()

        val request = ProductUsageRequest(
            nfcCardUid = nfcCardUid,
            productTourId = productTourId,
            quantity = quantity,
            clientRequestId = requestId
        )

        val payloadJson = gson.toJson(request)

        val command = PosCommandEntity(
            localId = localId,
            clientRequestId = requestId,
            operation = "PRODUCT_USAGE",
            payloadJson = payloadJson,
            status = PosCommandStatus.PENDING_SYNC.name
        )

        // Lưu lệnh trước khi gửi request lên backend.
        commandDao.insert(command)

        // Chỉ tiến trình nhận được claim mới được gửi request.
        val claimed = commandDao.claimForSync(localId)

        if (claimed == 0) {
            throw IllegalStateException(
                "Không thể nhận lệnh mua sản phẩm để đồng bộ."
            )
        }

        return try {
            val response = apiService.createProductUsage(request)

            commandDao.markConfirmed(localId)

            ProductUsageSubmissionResult.Confirmed(response)

        } catch (error: CancellationException) {
            commandDao.markPending(
                localId = localId,
                error = "Yêu cầu bị gián đoạn, cần đồng bộ lại."
            )

            PosCommandSyncWorker.schedule(appContext)

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
                val message = serverMessage
                    ?: "Máy chủ từ chối giao dịch. HTTP ${error.code()}"

                commandDao.markFailed(
                    localId = localId,
                    error = message
                )

                throw IllegalStateException(message, error)
            }

            commandDao.markPending(
                localId = localId,
                error = serverMessage
                    ?: "Chưa thể đồng bộ giao dịch. HTTP ${error.code()}"
            )

            PosCommandSyncWorker.schedule(appContext)

            ProductUsageSubmissionResult.Queued

        } catch (error: IOException) {
            commandDao.markPending(
                localId = localId,
                error = "Mất kết nối tới máy chủ. Giao dịch đã được lưu trên thiết bị."
            )

            PosCommandSyncWorker.schedule(appContext)

            ProductUsageSubmissionResult.Queued

        } catch (error: Exception) {
            commandDao.markFailed(
                localId = localId,
                error = error.message ?: "Không thể xử lý giao dịch."
            )

            throw error
        }
    }

    /**
     * SERVICE_SCAN vẫn sử dụng cơ chế idempotency cũ.
     * Không chuyển SERVICE_SCAN sang Room queue ở bước này.
     */
    suspend fun createServiceUsage(
        nfcCardUid: String,
        serviceTourId: String
    ): ServiceUsageResponse {

        val request = ServiceUsageRequest(
            nfcCardUid,
            serviceTourId
        )

        return commands.execute("SERVICE_SCAN", request) { requestId ->
            apiService.createServiceUsage(
                request.copy(clientRequestId = requestId)
            )
        }
    }
}
