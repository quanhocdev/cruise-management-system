package com.project.cruise.android.data.dto.passenger.feedback

import java.time.Instant
import java.util.UUID

data class FeedbackInvitationResponse(
    val id: UUID,
    val bookingId: Long,
    val bookingPassengerId: Long,
    val feedbackType: String,
    val targetId: UUID,
    val targetName: String,
    val status: String,
    val createdAt: Instant
)