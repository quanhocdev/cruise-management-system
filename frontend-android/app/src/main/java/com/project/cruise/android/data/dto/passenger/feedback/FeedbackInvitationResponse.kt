package com.project.cruise.android.data.dto.passenger.feedback

data class FeedbackInvitationResponse(
    val id: String,
    val bookingId: Long? = null,
    val bookingPassengerId: Long? = null,
    val feedbackType: String? = null,
    val targetId: String? = null,
    val targetName: String? = null,
    val status: String? = null,
    val createdAt: String? = null
)