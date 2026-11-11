package com.project.cruise.android.data.repository.feedback

import com.project.cruise.android.data.dto.passenger.feedback.FeedbackInvitationResponse
import com.project.cruise.android.data.dto.passenger.feedback.FeedbackResponse
import com.project.cruise.android.data.dto.passenger.feedback.SubmitFeedbackRequest
import com.project.cruise.android.data.network.api.FeedbackApiService
import java.util.UUID

interface FeedbackSource {
    suspend fun getMyInvitations(): List<FeedbackInvitationResponse>
    suspend fun getMyPendingInvitations(): List<FeedbackInvitationResponse>
    suspend fun submitFeedback(invitationId: UUID, request: SubmitFeedbackRequest): FeedbackResponse
    suspend fun skipInvitation(invitationId: UUID): FeedbackInvitationResponse
    suspend fun getMyFeedback(invitationId: UUID): FeedbackResponse
}

class FeedbackRepository(
    private val api: FeedbackApiService
) : FeedbackSource {

    override suspend fun getMyInvitations(): List<FeedbackInvitationResponse> {
        return api.getMyInvitations()
    }

    override suspend fun getMyPendingInvitations(): List<FeedbackInvitationResponse> {
        return api.getMyPendingInvitations()
    }

    override suspend fun submitFeedback(
        invitationId: UUID,
        request: SubmitFeedbackRequest
    ): FeedbackResponse {
        return api.submitFeedback(invitationId, request)
    }

    override suspend fun skipInvitation(invitationId: UUID): FeedbackInvitationResponse {
        return api.skipInvitation(invitationId)
    }

    override suspend fun getMyFeedback(invitationId: UUID): FeedbackResponse {
        return api.getMyFeedback(invitationId)
    }
}

// Các hàm nhãn hiển thị trạng thái và loại feedback
// Các hàm nhãn hiển thị trạng thái và loại feedback (hỗ trợ cả String lẫn Enum nếu có)
fun feedbackTypeLabel(type: Any?): String {
    val typeStr = type?.toString() ?: return "Đánh giá chung"
    return when (typeStr) {
        "TOUR" -> "Chuyến đi"
        "ONBOARD_ACTIVITY" -> "Hoạt động trên tàu"
        "SHORE_ACTIVITY" -> "Tham quan bờ"
        "SERVICE" -> "Dịch vụ trên tàu"
        "PRODUCT" -> "Sản phẩm trên tàu"
        else -> "Đánh giá chung"
    }
}

fun feedbackInvitationStatusLabel(status: Any?): String {
    val statusStr = status?.toString() ?: return "Không rõ"
    return when (statusStr) {
        "PENDING" -> "Đang chờ đánh giá"
        "SUBMITTED" -> "Đã hoàn thành"
        "SKIPPED" -> "Đã bỏ qua"
        else -> "Không rõ"
    }
}