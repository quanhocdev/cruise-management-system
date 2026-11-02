package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.data.local.CruiseDatabase

class OnboardPosRepository(context: Context) {
    private val transactionDao = CruiseDatabase.getInstance(context.applicationContext).posTransactionDao()

    suspend fun readTicketQr(localId: String): String {
        val scan = checkNotNull(transactionDao.findByLocalId(localId)) {
            "Không tìm thấy lượt quét QR trên thiết bị"
        }
        check(scan.operatorRole == "ONBOARD" && scan.scanType == "QR") {
            "Lượt quét không thuộc ONBOARD POS"
        }
        return scan.scannedValue
    }
}
