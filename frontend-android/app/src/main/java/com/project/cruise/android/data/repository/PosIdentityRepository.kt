package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.BuildConfig
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.local.CruiseDatabase
import com.project.cruise.android.data.network.PosIdentityRequest
import com.project.cruise.android.data.network.RetrofitClient

class PosIdentityRepository(context: Context, private val operatorRole: String) {
    private val appContext = context.applicationContext
    private val dao = CruiseDatabase.getInstance(appContext).posTransactionDao()
    private val posApi = RetrofitClient.createPosApiService(TokenManager(appContext))
    suspend fun identify(localId: String): com.project.cruise.android.data.network.PosIdentityResponse {
        check(BuildConfig.POS_API_KEY.isNotBlank()) { "Chưa cấu hình key thiết bị POS" }
        val scan = checkNotNull(dao.findByLocalId(localId)) { "Không tìm thấy bản ghi quét" }
        check(scan.terminalCode == BuildConfig.POS_TERMINAL_CODE) { "Bản ghi thuộc thiết bị POS khác" }
        check(scan.operatorRole == operatorRole) { "Bản ghi thuộc role POS khác" }
        return posApi.identify(
            BuildConfig.POS_TERMINAL_CODE, BuildConfig.POS_API_KEY,
            PosIdentityRequest(scan.scanType, scan.scannedValue)
        )
    }

    suspend fun checkIn(localId: String): com.project.cruise.android.data.network.PosCheckInResponse {
        check(operatorRole == "FINANCE") { "Chỉ FINANCE được thực hiện check-in" }
        check(BuildConfig.POS_API_KEY.isNotBlank()) { "Chưa cấu hình key thiết bị POS" }
        val scan = checkNotNull(dao.findByLocalId(localId)) { "Không tìm thấy bản ghi quét" }
        check(scan.terminalCode == BuildConfig.POS_TERMINAL_CODE) { "Bản ghi thuộc thiết bị POS khác" }
        check(scan.operatorRole == operatorRole) { "Bản ghi thuộc role POS khác" }
        return posApi.checkIn(
            BuildConfig.POS_TERMINAL_CODE, BuildConfig.POS_API_KEY,
            PosIdentityRequest(scan.scanType, scan.scannedValue)
        )
    }
}
