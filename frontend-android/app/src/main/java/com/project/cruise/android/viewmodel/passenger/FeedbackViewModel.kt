package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.feedback.MyFeedback
import com.project.cruise.android.data.repository.FeedbackSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class FeedbackState(
    val loading: Boolean = true,
    val items: List<MyFeedback> = emptyList(),
    val error: String? = null,
)

class FeedbackViewModel(private val source: FeedbackSource) : ViewModel() {
    private val mutableState = MutableStateFlow(FeedbackState())
    val state = mutableState.asStateFlow()
    private var request: Job? = null

    fun load(bookingId: Long) {
        request?.cancel()
        mutableState.value = FeedbackState()
        request = viewModelScope.launch {
            try {
                mutableState.value = FeedbackState(loading = false, items = source.mine(bookingId))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mutableState.value = FeedbackState(
                    loading = false,
                    error = if (e is HttpException && e.code() in setOf(401, 403))
                        "Vui lòng đăng nhập bằng tài khoản hành khách để xem đánh giá."
                    else "Chưa tải được đánh giá. Vui lòng thử lại.",
                )
            }
        }
    }

    class Factory(private val source: FeedbackSource) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = FeedbackViewModel(source) as T
    }
}
