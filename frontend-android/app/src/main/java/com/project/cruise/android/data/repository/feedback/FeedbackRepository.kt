package com.project.cruise.android.data.repository.feedback

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.project.cruise.android.data.dto.passenger.feedback.FeedbackInvitationResponse
import com.project.cruise.android.data.dto.passenger.feedback.FeedbackResponse
import com.project.cruise.android.data.dto.passenger.feedback.SubmitFeedbackRequest
import com.project.cruise.android.data.network.api.FeedbackApiService

interface FeedbackSource {
    suspend fun getMyInvitations(): List<FeedbackInvitationResponse>
    suspend fun getMyPendingInvitations(): List<FeedbackInvitationResponse>
    suspend fun submitFeedback(invitationId: String, request: SubmitFeedbackRequest): FeedbackResponse
    suspend fun skipInvitation(invitationId: String): FeedbackInvitationResponse
    suspend fun getMyFeedback(invitationId: String): FeedbackResponse
}

class FeedbackRepository(
    private val api: FeedbackApiService,
    private val gson: Gson = Gson()
) : FeedbackSource {

    private fun unwrapPayload(raw: JsonElement): JsonElement {
        if (raw.isJsonObject) {
            val obj = raw.asJsonObject
            if (obj.has("data") && !obj.get("data").isJsonNull) return obj.get("data")
        }
        return raw
    }

    private fun parseInvitationList(raw: JsonElement): List<FeedbackInvitationResponse> {
        val payload = unwrapPayload(raw)
        val arrayElement = when {
            payload.isJsonArray -> payload
            payload.isJsonObject && payload.asJsonObject.has("content") ->
                payload.asJsonObject.get("content")
            else -> null
        } ?: return emptyList()

        val type = object : TypeToken<List<FeedbackInvitationResponse>>() {}.type
        return gson.fromJson(arrayElement, type) ?: emptyList()
    }

    override suspend fun getMyInvitations(): List<FeedbackInvitationResponse> {
        return parseInvitationList(api.getMyInvitations())
    }

    override suspend fun getMyPendingInvitations(): List<FeedbackInvitationResponse> {
        return parseInvitationList(api.getMyPendingInvitations())
    }

    override suspend fun submitFeedback(
        invitationId: String,
        request: SubmitFeedbackRequest
    ): FeedbackResponse {
        val payload = unwrapPayload(api.submitFeedback(invitationId, request))
        return gson.fromJson(payload, FeedbackResponse::class.java)
    }

    override suspend fun skipInvitation(invitationId: String): FeedbackInvitationResponse {
        val payload = unwrapPayload(api.skipInvitation(invitationId))
        return gson.fromJson(payload, FeedbackInvitationResponse::class.java)
    }

    override suspend fun getMyFeedback(invitationId: String): FeedbackResponse {
        val payload = unwrapPayload(api.getMyFeedback(invitationId))
        return gson.fromJson(payload, FeedbackResponse::class.java)
    }
}

fun feedbackTypeLabel(type: Any?): String {
    return when (type?.toString()?.uppercase()) {
        "PRODUCT" -> "Sản phẩm"
        "SERVICE" -> "Dịch vụ"
        "ACTIVITY_CRUISE", "ONBOARD_ACTIVITY" -> "Hoạt động trên tàu"
        "ACTIVITY_VISIT", "SHORE_ACTIVITY" -> "Hoạt động tham quan"
        "TOUR" -> "Chuyến đi"
        else -> type?.toString() ?: "Đánh giá chung"
    }
}

fun feedbackInvitationStatusLabel(status: Any?): String {
    return when (status?.toString()?.uppercase()) {
        "PENDING" -> "Chưa đánh giá"
        "COMPLETED", "SUBMITTED" -> "Đã đánh giá"
        "SKIPPED" -> "Đã bỏ qua"
        else -> status?.toString() ?: "Không rõ"
    }
}