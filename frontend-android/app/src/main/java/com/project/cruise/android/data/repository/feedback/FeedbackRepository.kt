package com.project.cruise.android.data.repository.feedback

import android.content.Context
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.feedback.*
import com.project.cruise.android.data.network.api.FeedbackApiService
import com.project.cruise.android.data.repository.pos.OnlinePosCommands
import retrofit2.HttpException

fun interface FeedbackSource {
    suspend fun mine(bookingId: Long): List<MyFeedback>
    suspend fun options(bookingId: Long): FeedbackOptions? = null
    fun pending(bookingId: Long): FeedbackDraft? = null
    suspend fun submit(draft: FeedbackDraft): MyFeedback = error("Chưa hỗ trợ gửi đánh giá")
}

class FeedbackRepository(
    private val api: FeedbackApiService, // Đổi sang FeedbackApiService
    context: Context? = null
) : FeedbackSource {
    private val commands = context?.let { OnlinePosCommands(it.applicationContext) }
    private val preferences = context?.applicationContext?.getSharedPreferences("feedback_drafts", Context.MODE_PRIVATE)
    private val tokens = context?.let { TokenManager(it.applicationContext) }

    private fun slot(id: Long): String {
        val token = requireNotNull(tokens?.getAccessToken())
        val claims = JsonParser().parse(String(Base64.decode(token.split('.')[1], Base64.URL_SAFE), Charsets.UTF_8)).asJsonObject
        val actor = (claims.get("userId") ?: claims.get("sub")).asString
        return "$actor:$id"
    }

    override suspend fun options(bookingId: Long) = api.getFeedbackOptions(bookingId)

    override fun pending(bookingId: Long): FeedbackDraft? = preferences?.getString(slot(bookingId), null)?.let {
        Gson().fromJson(it, FeedbackDraft::class.java)
    }

    override suspend fun submit(draft: FeedbackDraft): MyFeedback {
        val store = requireNotNull(preferences)
        val slot = slot(draft.bookingId)
        val previous = pending(draft.bookingId)
        check(previous == null || previous == draft) { "Yêu cầu trước chưa rõ kết quả. Vui lòng gửi lại nội dung đã lưu." }
        check(store.edit().putString(slot, Gson().toJson(draft)).commit()) { "Không lưu được nội dung đánh giá." }
        try {
            val result = requireNotNull(commands).execute("FEEDBACK:${draft.bookingId}", draft) { key -> api.createFeedback(draft.copy(clientRequestId = key)) }
            check(store.edit().remove(slot).commit()) { "Đã gửi nhưng chưa xóa được bản nháp. Vui lòng tải lại lịch sử." }
            return result
        } catch (e: HttpException) {
            if (e.code() in setOf(400, 401, 403, 404, 422)) store.edit().remove(slot).commit()
            throw e
        }
    }

    override suspend fun mine(bookingId: Long): List<MyFeedback> =
        api.getMyFeedback().filter { it.bookingId == bookingId && it.status != "DELETED" }
}

fun feedbackAvailability(state: String) = when (state) {
    "READY" -> "Bạn có thể chia sẻ trải nghiệm chung của chuyến đi."
    "ALREADY_REVIEWED" -> "Bạn đã đánh giá chuyến đi này. Xem nội dung bên dưới."
    "PASSENGER_NOT_COMPLETED" -> "Đánh giá mở sau khi Finance xác nhận hành khách đã rời tàu."
    "TOUR_NOT_COMPLETED" -> "Chuyến đi chưa được xác nhận hoàn thành."
    else -> "Chưa xác minh được điều kiện đánh giá. Vui lòng thử lại sau."
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