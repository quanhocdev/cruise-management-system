package com.project.cruise.android.data.repository

import com.project.cruise.android.data.dto.feedback.MyFeedback
import com.project.cruise.android.data.network.ApiService

fun interface FeedbackSource {
    suspend fun mine(bookingId: Long): List<MyFeedback>
}

class FeedbackRepository(private val api: ApiService) : FeedbackSource {
    override suspend fun mine(bookingId: Long): List<MyFeedback> =
        api.getMyFeedback().filter { it.bookingId == bookingId && it.status != "DELETED" }
}

fun feedbackTargetLabel(type: String?) = when (type) {
    "TOUR" -> "Trải nghiệm chuyến đi"
    "ONBOARD_ACTIVITY" -> "Hoạt động trên tàu"
    "SHORE_ACTIVITY" -> "Tham quan bờ"
    "SERVICE" -> "Dịch vụ trên tàu"
    "PRODUCT" -> "Sản phẩm trên tàu"
    else -> "Đánh giá chuyến đi"
}

fun feedbackStatusLabel(status: String) = when (status) {
    "PUBLISHED" -> "Đã đăng"
    "HIDDEN" -> "Đang ẩn"
    "REMOVED" -> "Đã gỡ"
    else -> "Đang cập nhật"
}
