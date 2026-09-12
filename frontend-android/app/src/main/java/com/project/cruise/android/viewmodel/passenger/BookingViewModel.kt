package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.booking.BookingResponse
import com.project.cruise.android.data.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

class BookingViewModel(
    private val repository: BookingRepository
) : ViewModel() {

    private val _bookingListState = MutableStateFlow<BookingListState>(BookingListState.Idle)
    val bookingListState: StateFlow<BookingListState> = _bookingListState

    private val _bookingDetailState = MutableStateFlow<BookingDetailState>(BookingDetailState.Idle)
    val bookingDetailState: StateFlow<BookingDetailState> = _bookingDetailState

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
        viewModelScope.launch {
            _bookingDetailState.value = BookingDetailState.Loading
            try {
                val detail = repository.getBookingById(id)
                _bookingDetailState.value = BookingDetailState.Success(detail)
            } catch (e: Exception) {
                _bookingDetailState.value = BookingDetailState.Error(e.message ?: "Tải chi tiết booking thất bại")
            }
        }
    }
}