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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

enum class FeedbackFilterKey(val label: String) {
    ALL("Tất cả"),
    PENDING("Chưa đánh giá"),
    COMPLETED("Đã đánh giá"),
    SKIPPED("Đã bỏ qua")
}

data class FeedbackState(
    val loading: Boolean = false,
    val detailLoading: Boolean = false,
    val sending: Boolean = false,
    val activeFilter: FeedbackFilterKey = FeedbackFilterKey.ALL,
    val invitations: List<FeedbackInvitationResponse> = emptyList(),
    val selectedFeedback: FeedbackResponse? = null,
    val error: String? = null,
    val message: String? = null
)

class FeedbackViewModel(private val source: FeedbackSource) : ViewModel() {
    private val mutableState = MutableStateFlow(FeedbackState())
    val state = mutableState.asStateFlow()

    private var loadJob: Job? = null
    private var actionJob: Job? = null

    fun setFilter(filter: FeedbackFilterKey) {
        mutableState.update { it.copy(activeFilter = filter) }
    }

    fun clearMessages() {
        mutableState.update { it.copy(error = null, message = null) }
    }

    fun clearSelectedFeedback() {
        mutableState.update { it.copy(selectedFeedback = null, detailLoading = false) }
    }

    fun loadInvitations() {
        loadJob?.cancel()
        mutableState.update { it.copy(loading = true, error = null) }
        loadJob = viewModelScope.launch {
            try {
                val invitations = source.getMyInvitations()
                mutableState.update {
                    it.copy(
                        loading = false,
                        invitations = invitations
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mutableState.update {
                    it.copy(
                        loading = false,
                        error = if (e is HttpException && e.code() in setOf(401, 403))
                            "Vui lòng đăng nhập lại để xem lời mời đánh giá."
                        else "Không thể tải danh sách đánh giá. Vui lòng thử lại."
                    )
                }
            }
        }
    }

    fun submitFeedback(
        invitationId: String,
        rating: Int,
        comment: String,
        onSuccess: () -> Unit = {}
    ) {
        if (rating !in 1..5) {
            mutableState.update { it.copy(error = "Vui lòng chọn số sao từ 1 đến 5.") }
            return
        }
        if (comment.length > 2000) {
            mutableState.update { it.copy(error = "Bình luận không được vượt quá 2000 ký tự.") }
            return
        }

        actionJob?.cancel()
        mutableState.update { it.copy(sending = true, error = null, message = null) }
        actionJob = viewModelScope.launch {
            try {
                val request = SubmitFeedbackRequest(
                    rating = rating,
                    comment = comment.trim().ifBlank { null }
                )
                val submitted = source.submitFeedback(invitationId, request)
                val updatedList = source.getMyInvitations()
                mutableState.update {
                    it.copy(
                        sending = false,
                        invitations = updatedList,
                        selectedFeedback = submitted,
                        message = "Gửi đánh giá thành công!"
                    )
                }
                onSuccess()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mutableState.update {
                    it.copy(
                        sending = false,
                        error = "Gửi đánh giá thất bại. Vui lòng thử lại."
                    )
                }
            }
        }
    }

    fun skipInvitation(invitationId: String) {
        actionJob?.cancel()
        mutableState.update { it.copy(sending = true, error = null, message = null) }
        actionJob = viewModelScope.launch {
            try {
                source.skipInvitation(invitationId)
                val updatedList = source.getMyInvitations()
                mutableState.update {
                    it.copy(
                        sending = false,
                        invitations = updatedList,
                        message = "Đã bỏ qua lời mời đánh giá."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mutableState.update {
                    it.copy(
                        sending = false,
                        error = "Không thể bỏ qua lời mời đánh giá."
                    )
                }
            }
        }
    }

    fun loadFeedbackDetail(invitationId: String) {
        actionJob?.cancel()
        mutableState.update { it.copy(detailLoading = true, selectedFeedback = null, error = null) }
        actionJob = viewModelScope.launch {
            try {
                val feedback = source.getMyFeedback(invitationId)
                mutableState.update {
                    it.copy(
                        detailLoading = false,
                        selectedFeedback = feedback
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mutableState.update {
                    it.copy(
                        detailLoading = false,
                        error = "Không thể tải nội dung đánh giá."
                    )
                }
            }
        }
    }

    class Factory(private val source: FeedbackSource) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FeedbackViewModel(source) as T
        }
    }
}