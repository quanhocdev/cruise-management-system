package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.BuildConfig
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.local.CruiseDatabase
import com.project.cruise.android.data.network.QrScanRequest
import com.project.cruise.android.data.network.RetrofitClient
import com.project.cruise.android.data.network.ScanResponse

class PosIdentityRepository(context: Context, private val operatorRole: String) {
    private val appContext = context.applicationContext
    private val dao = CruiseDatabase.getInstance(appContext).posTransactionDao()
    private val posApi = RetrofitClient.createPosApiService(TokenManager(appContext))

    suspend fun scanAndNotify(localId: String): ScanResponse {
        val scan = checkNotNull(dao.findByLocalId(localId)) { "Không tìm thấy bản ghi quét" }
        check(scan.terminalCode == BuildConfig.POS_TERMINAL_CODE) { "Bản ghi thuộc thiết bị POS khác" }
        check(scan.operatorRole == operatorRole) { "Bản ghi thuộc role POS khác" }

        // Gọi API /api/finance/scan đã được tối giản ở Backend
        return posApi.scanQrCode(
            QrScanRequest(bookingCode = scan.scannedValue)
        )
    }
}