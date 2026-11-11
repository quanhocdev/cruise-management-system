package com.project.cruise.android.data.dto.passenger.feedback

import java.time.Instant
import java.util.UUID

data class FeedbackInvitationResponse(
    val id: UUID,
    val bookingId: Long?,
    val bookingPassengerId: Long?,
    val feedbackType: String?, // Hoặc Enum tương ứng nếu có
    val targetId: UUID?,
    val targetName: String?,
    val status: String?, // PENDING, SKIPPED, SUBMITTED...
    val createdAt: Instant?
)