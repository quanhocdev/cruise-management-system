package com.project.cruise.android.data.dto.passenger

data class PassengerNotification(
    val id: Long,
    val type: String?,
    val title: String,
    val message: String,
    val referenceType: String?,
    val referenceId: Long?,
    val readAt: String?,
    val createdAt: String?
)

data class UnreadNotifications(val unreadCount: Long)

fun PassengerNotification.bookingId(): Long? =
    referenceId?.takeIf { referenceType == "BOOKING" && it > 0 }
