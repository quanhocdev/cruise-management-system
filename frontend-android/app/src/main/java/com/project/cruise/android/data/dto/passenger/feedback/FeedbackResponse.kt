package com.project.cruise.android.data.dto.passenger.feedback

data class FeedbackResponse(
    val id: String? = null,
    val invitationId: String? = null,
    val bookingId: Long? = null,
    val bookingPassengerId: Long? = null,
    val feedbackType: String? = null,
    val targetId: String? = null,
    val targetName: String? = null,
    val rating: Int? = null,
    val comment: String? = null,
    val createdAt: String? = null
)