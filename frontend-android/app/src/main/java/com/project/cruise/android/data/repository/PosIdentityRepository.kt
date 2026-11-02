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

    suspend fun isLocalReadOnly(localId: String): Boolean {
        val scan = checkNotNull(dao.findByLocalId(localId)) { "Không tìm thấy bản ghi quét" }
        check(scan.operatorRole == operatorRole && scan.terminalCode == BuildConfig.POS_TERMINAL_CODE)
        return operatorRole != "FINANCE" || scan.scanType != "QR"
    }

    suspend fun scanAndNotify(localId: String): ScanResponse {
        val scan = checkNotNull(dao.findByLocalId(localId)) { "Không tìm thấy bản ghi quét" }
        check(scan.terminalCode == BuildConfig.POS_TERMINAL_CODE) { "Bản ghi thuộc thiết bị POS khác" }
        check(scan.operatorRole == operatorRole) { "Bản ghi thuộc role POS khác" }

        check(operatorRole == "FINANCE" && scan.scanType == "QR") {
            "Đã lưu mã vòng trên thiết bị. Chưa xác nhận danh tính hoặc quyền tham gia của hành khách."
        }

        // Gọi API /api/finance/scan đã được tối giản ở Backend
        return posApi.scanQrCode(
            QrScanRequest(bookingCode = scan.scannedValue)
        )
    }
}