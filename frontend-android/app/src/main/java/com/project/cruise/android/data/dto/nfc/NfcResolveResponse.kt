package com.project.cruise.android.data.dto.nfc

data class NfcResolveResponse(
    val bookingPassengerId: Long,
    val passengerName: String?,
    val bookingId: Long,
    val tourId: String,
    val tourPackageId: String
)