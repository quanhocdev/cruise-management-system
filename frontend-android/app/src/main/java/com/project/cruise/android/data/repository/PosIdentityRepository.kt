package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.BuildConfig
import com.project.cruise.android.data.local.CruiseDatabase
import com.project.cruise.android.data.network.PosIdentityRequest
import com.project.cruise.android.data.network.RetrofitClient

class PosIdentityRepository(context: Context) {
    private val dao = CruiseDatabase.getInstance(context.applicationContext).posTransactionDao()
    suspend fun identify(localId: String): com.project.cruise.android.data.network.PosIdentityResponse {
        check(BuildConfig.POS_API_KEY.isNotBlank()) { "Chưa cấu hình key thiết bị POS" }
        val scan = checkNotNull(dao.findByLocalId(localId)) { "Không tìm thấy bản ghi quét" }
        check(scan.terminalCode == BuildConfig.POS_TERMINAL_CODE) { "Bản ghi thuộc thiết bị POS khác" }
        return RetrofitClient.posApiService.identify(
            BuildConfig.POS_TERMINAL_CODE, BuildConfig.POS_API_KEY,
            PosIdentityRequest(scan.scanType, scan.scannedValue)
        )
    }
}
