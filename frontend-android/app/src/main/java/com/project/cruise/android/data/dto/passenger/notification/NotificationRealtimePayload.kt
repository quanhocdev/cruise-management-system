package com.project.cruise.android.data.dto.passenger.notification

data class NotificationRealtimePayload(
    val eventType: String?,
    val notification: NotificationResponse?,
    val unreadCount: Long
)