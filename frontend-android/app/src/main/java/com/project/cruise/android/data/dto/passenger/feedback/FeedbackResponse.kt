package com.project.cruise.android.data.dto.passenger.feedback

import java.time.Instant
import java.util.UUID

data class FeedbackResponse(
    val id: UUID,
    val invitationId: UUID?,
    val bookingId: Long?,
    val bookingPassengerId: Long?,
    val feedbackType: String?,
    val targetId: UUID?,
    val targetName: String?,
    val rating: Int?,
    val comment: String?,
    val createdAt: Instant?
)