package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.feedback.*
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
    val options: FeedbackOptions? = null,
    val optionsError: String? = null,
    val pending: FeedbackDraft? = null,
    val sending: Boolean = false,
    val message: String? = null,
)

class FeedbackViewModel(private val source: FeedbackSource) : ViewModel() {
    private val mutableState = MutableStateFlow(FeedbackState())
    val state = mutableState.asStateFlow()
    private var request: Job? = null

    fun load(bookingId: Long) {
        if(mutableState.value.sending)return
        request?.cancel()
        mutableState.value = FeedbackState()
        request = viewModelScope.launch {
            try {
                val items=source.mine(bookingId)
                val pending=source.pending(bookingId)
                mutableState.value = FeedbackState(loading = false, items = items,pending=pending)
                try { mutableState.value=mutableState.value.copy(options=source.options(bookingId)) }
                catch(e: CancellationException){throw e}
                catch(e: Exception){mutableState.value=mutableState.value.copy(optionsError="Chưa kiểm tra được điều kiện đánh giá. Vui lòng tải lại.")}
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

    fun submit(draft: FeedbackDraft) {
        val state=mutableState.value
        if(state.loading || state.sending || draft.rating !in 1..5 || draft.content.isBlank() || draft.content.length>5000)return
        if(state.pending==null && (state.options?.state!="READY" || state.options.bookingId!=draft.bookingId ||
            state.options.passengers.none{it.bookingPassengerId==draft.bookingPassengerId && it.participated}))return
        if(state.pending!=null && state.pending!=draft)return
        request?.cancel()
        mutableState.value=state.copy(sending=true,error=null,message=null,pending=draft)
        request=viewModelScope.launch {
            try {
                val review=source.submit(draft)
                mutableState.value=mutableState.value.copy(sending=false,pending=null,
                    items=listOf(review)+mutableState.value.items.filter{it.id!=review.id},
                    options=mutableState.value.options?.copy(state="ALREADY_REVIEWED"),message="Đã gửi đánh giá. Cảm ơn bạn đã chia sẻ!")
            } catch(e: CancellationException){throw e}
            catch(e: Exception){
                val definitive=e is HttpException && e.code() in setOf(400,401,403,404,422)
                mutableState.value=mutableState.value.copy(sending=false,pending=if(definitive)null else draft,
                    message=if(definitive)"Chưa thể gửi đánh giá. Hãy cập nhật điều kiện chuyến đi và kiểm tra nội dung."
                    else "Chưa nhận được kết quả. Nội dung đã được giữ lại; hãy gửi lại đúng bản này hoặc kiểm tra lịch sử.")
            }
        }
    }

    class Factory(private val source: FeedbackSource) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = FeedbackViewModel(source) as T
    }
}
