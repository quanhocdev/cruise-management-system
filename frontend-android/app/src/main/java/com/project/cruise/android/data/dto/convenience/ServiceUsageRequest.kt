package com.project.cruise.android.data.dto.convenience

data class ServiceUsageRequest(
    val nfcCardUid: String,
    val serviceTourId: String,
    val clientRequestId: String? = null
)