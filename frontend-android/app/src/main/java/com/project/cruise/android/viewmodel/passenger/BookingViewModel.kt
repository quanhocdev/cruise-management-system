package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.booking.BookingResponse
import com.project.cruise.android.data.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import com.project.cruise.android.data.dto.booking.BookingPayment
import com.project.cruise.android.data.dto.booking.BookingStatus

sealed class BookingListState {
    object Idle : BookingListState()
    object Loading : BookingListState()
    data class Success(val bookings: List<BookingResponse>) : BookingListState()
    data class Error(val message: String) : BookingListState()
}

sealed class BookingDetailState {
    object Idle : BookingDetailState()
    object Loading : BookingDetailState()
    data class Success(val booking: BookingResponse) : BookingDetailState()
    data class Error(val message: String) : BookingDetailState()
}

data class BookingAccessState(
    val loading: Boolean = false,
    val payment: BookingPayment? = null,
    val qrBytes: ByteArray? = null,
    val message: String? = null
)

class BookingViewModel(
    private val repository: BookingRepository
) : ViewModel() {

    private val _bookingListState = MutableStateFlow<BookingListState>(BookingListState.Idle)
    val bookingListState: StateFlow<BookingListState> = _bookingListState

    private val _bookingDetailState = MutableStateFlow<BookingDetailState>(BookingDetailState.Idle)
    val bookingDetailState: StateFlow<BookingDetailState> = _bookingDetailState

    private val _bookingAccessState = MutableStateFlow(BookingAccessState())
    val bookingAccessState: StateFlow<BookingAccessState> = _bookingAccessState
    private var detailJob: Job? = null

    fun fetchMyBookings() {
        viewModelScope.launch {
            _bookingListState.value = BookingListState.Loading
            try {
                val list = repository.getMyBookings()
                _bookingListState.value = BookingListState.Success(list)
            } catch (e: Exception) {
                _bookingListState.value = BookingListState.Error(e.message ?: "Tải danh sách booking thất bại")
            }
        }
    }

    fun fetchBookingDetail(id: Long) {
        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            _bookingDetailState.value = BookingDetailState.Loading
            _bookingAccessState.value = BookingAccessState()
            val detail = try {
                repository.getBookingById(id)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _bookingDetailState.value = BookingDetailState.Error(error.message ?: "Tải chi tiết booking thất bại")
                return@launch
            }
            _bookingDetailState.value = BookingDetailState.Success(detail)
            if (detail.status == BookingStatus.CANCELLED) return@launch
            _bookingAccessState.value = BookingAccessState(loading = true)
            try {
                _bookingAccessState.value = when (detail.status) {
                    BookingStatus.CONFIRMED -> BookingAccessState(qrBytes = repository.getQr(id))
                    BookingStatus.PENDING_PAYMENT -> BookingAccessState(payment = repository.getPayment(id))
                    else -> BookingAccessState()
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                val preparing = detail.status == BookingStatus.PENDING_PAYMENT &&
                    error is retrofit2.HttpException && error.code() == 404
                _bookingAccessState.value = BookingAccessState(message = if (preparing)
                    "Hệ thống đang chuẩn bị giao dịch. Bấm cập nhật sau ít giây."
                    else "Chưa tải được vé hoặc thanh toán. Vui lòng thử cập nhật lại.")
            }
        }
    }
}
