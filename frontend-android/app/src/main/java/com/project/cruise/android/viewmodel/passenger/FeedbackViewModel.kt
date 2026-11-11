package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.passenger.feedback.FeedbackInvitationResponse
import com.project.cruise.android.data.dto.passenger.feedback.FeedbackResponse
import com.project.cruise.android.data.dto.passenger.feedback.SubmitFeedbackRequest
import com.project.cruise.android.data.repository.feedback.FeedbackSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.UUID

data class FeedbackState(
    val loading: Boolean = false,
    val invitations: List<FeedbackInvitationResponse> = emptyList(),
    val pendingInvitations: List<FeedbackInvitationResponse> = emptyList(),
    val selectedFeedback: FeedbackResponse? = null,
    val sending: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

class FeedbackViewModel(private val source: FeedbackSource) : ViewModel() {
    private val mutableState = MutableStateFlow(FeedbackState())
    val state = mutableState.asStateFlow()
    private var job: Job? = null

    // Tải tất cả lời mời đánh giá
    fun loadInvitations() {
        job?.cancel()
        mutableState.value = mutableState.value.copy(loading = true, error = null)
        job = viewModelScope.launch {
            try {
                val invitations = source.getMyInvitations()
                val pending = source.getMyPendingInvitations()
                mutableState.value = mutableState.value.copy(
                    loading = false,
                    invitations = invitations,
                    pendingInvitations = pending
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mutableState.value = mutableState.value.copy(
                    loading = false,
                    error = if (e is HttpException && e.code() in setOf(401, 403))
                        "Vui lòng đăng nhập lại để xem lời mời đánh giá."
                    else "Không thể tải danh sách đánh giá. Vui lòng thử lại."
                )
            }
        }
    }

    // Gửi đánh giá cho một lời mời
    fun submitFeedback(invitationId: UUID, rating: Int, comment: String) {
        if (rating !in 1..5) {
            mutableState.value = mutableState.value.copy(message = "Vui lòng chọn số sao từ 1 đến 5.")
            return
        }

        job?.cancel()
        mutableState.value = mutableState.value.copy(sending = true, message = null)
        job = viewModelScope.launch {
            try {
                val request = SubmitFeedbackRequest(rating = rating, comment = comment)
                source.submitFeedback(invitationId, request)

                // Sau khi gửi thành công, tải lại danh sách
                loadInvitations()
                mutableState.value = mutableState.value.copy(
                    sending = false,
                    message = "Gửi đánh giá thành công! Cảm ơn bạn."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mutableState.value = mutableState.value.copy(
                    sending = false,
                    message = "Gửi đánh giá thất bại. Vui lòng kiểm tra lại."
                )
            }
        }
    }

    // Bỏ qua lời mời
    fun skipInvitation(invitationId: UUID) {
        viewModelScope.launch {
            try {
                source.skipInvitation(invitationId)
                loadInvitations()
            } catch (_: Exception) {}
        }
    }

    class Factory(private val source: FeedbackSource) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FeedbackViewModel(source) as T
        }
    }
}