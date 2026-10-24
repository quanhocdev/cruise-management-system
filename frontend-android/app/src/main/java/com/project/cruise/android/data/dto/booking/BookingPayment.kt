package com.project.cruise.android.data.dto.booking

import java.net.URI
import java.time.Instant

data class BookingPayment(
    val id: Long,
    val referenceId: Long,
    val payerId: String?,
    val status: String?,
    val paymentUrl: String?,
    val expiresAt: String?
) {
    fun payableUrl(now: Instant = Instant.now()): String? {
        if (status != "PENDING") return null
        val expiry = runCatching { Instant.parse(expiresAt) }.getOrNull() ?: return null
        if (!expiry.isAfter(now)) return null
        val uri = runCatching { URI(paymentUrl ?: return null) }.getOrNull() ?: return null
        return paymentUrl?.takeIf {
            uri.scheme == "https" && uri.host == "sandbox.vnpayment.vn" && uri.userInfo == null &&
                (uri.port == -1 || uri.port == 443)
        }
    }
}
