package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.local.CruiseDatabase
import com.project.cruise.android.data.network.ConvenienceProductResponse
import com.project.cruise.android.data.network.ConvenienceServiceResponse
import com.project.cruise.android.data.network.RetrofitClient

class ConveniencePosRepository(context: Context) {
    private val appContext = context.applicationContext
    private val transactionDao = CruiseDatabase.getInstance(appContext).posTransactionDao()
    private val api = RetrofitClient.createPosApiService(TokenManager(appContext))

    suspend fun readNfcUid(localId: String): String {
        val scan = checkNotNull(transactionDao.findByLocalId(localId)) {
            "Không tìm thấy lượt đọc NFC trên thiết bị"
        }
        check(scan.operatorRole == "CONVENIENCE" && scan.scanType == "NFC") {
            "Lượt đọc không thuộc POS tiện ích"
        }
        return scan.scannedValue
    }

    suspend fun getProducts(): List<ConvenienceProductResponse> =
        api.getConvenienceProducts()

    suspend fun getServices(): List<ConvenienceServiceResponse> =
        api.getConvenienceServices()
}
