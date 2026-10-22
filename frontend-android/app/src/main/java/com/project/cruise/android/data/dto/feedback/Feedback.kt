package com.project.cruise.android.data.dto.feedback

data class FeedbackGuest(val bookingPassengerId: Long,val name: String,val participated: Boolean)
data class FeedbackOptions(val bookingId: Long,val tourId: String,val state: String,val passengers: List<FeedbackGuest>)
data class FeedbackDraft(val bookingId: Long,val bookingPassengerId: Long,val rating: Int,val content: String,
    val feedbackType: String="TRIP",val clientRequestId: String?=null)

data class MyFeedback(
    val id: Long,
    val bookingId: Long,
    val feedbackType: String?,
    val targetType: String?,
    val targetId: String?,
    val rating: Int,
    val content: String,
    val imageUrls: List<String>?,
    val status: String,
    val moderationReason: String?,
    val createdAt: String?,
)
