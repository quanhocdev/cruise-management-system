package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.local.CruiseDatabase
import com.project.cruise.android.data.network.RetrofitClient
import com.project.cruise.android.data.network.ShoreVisitTourResponse

class ShorePosRepository(context: Context) {
    private val appContext = context.applicationContext
    private val transactionDao = CruiseDatabase.getInstance(appContext).posTransactionDao()
    private val api = RetrofitClient.createPosApiService(TokenManager(appContext))

    suspend fun readTicketQr(localId: String): String {
        val scan = checkNotNull(transactionDao.findByLocalId(localId)) {
            "Không tìm thấy lượt quét QR trên thiết bị"
        }
        check(scan.operatorRole == "SHORE" && scan.scanType == "QR") {
            "Lượt quét không thuộc SHORE POS"
        }
        return scan.scannedValue
    }

    suspend fun getVisitTours(): List<ShoreVisitTourResponse> = api.getShoreVisitTours()
}
