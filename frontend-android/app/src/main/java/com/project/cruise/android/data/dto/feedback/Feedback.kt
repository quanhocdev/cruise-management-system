package com.project.cruise.android.data.dto.feedback

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
