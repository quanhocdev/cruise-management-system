package com.project.cruise.android.data.dto.convenience

data class ProductUsageRequest(
    val nfcCardUid: String,
    val productTourId: String,
    val quantity: Int
)