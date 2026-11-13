package com.project.cruise.android.data.dto.passenger.notification

import java.time.Instant
import java.util.UUID

data class NotificationResponse(
    val id: UUID,
    val type: String?, // Hoặc Enum nếu có
    val title: String?,
    val message: String?,
    val referenceType: String?,
    val referenceId: UUID?,
    val isRead: Boolean,
    val createdAt: Instant?
)