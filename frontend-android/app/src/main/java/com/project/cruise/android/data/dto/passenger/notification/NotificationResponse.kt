package com.project.cruise.android.data.dto.passenger.notification

data class NotificationResponse(
    val id: String,
    val type: String? = null,
    val title: String? = null,
    val message: String? = null,
    val referenceType: String? = null,
    val referenceId: String? = null,
    val isRead: Boolean = false,
    val createdAt: String? = null
)