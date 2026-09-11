package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.passenger.AvailableRoom
import com.project.cruise.android.data.dto.passenger.PassengerBookingResponse
import com.project.cruise.android.data.repository.PassengerBookingRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class PassengerBookingState(
    val loading: Boolean = true,
    val submitting: Boolean = false,
    val room: AvailableRoom? = null,
    val draft: BookingDraft = BookingDraft(),
    val error: String? = null,
    val outcomeUnknown: Boolean = false,
    val booking: PassengerBookingResponse? = null
)

class PassengerBookingViewModel(
    private val repository: PassengerBookingRepository,
    private val voyageId: String,
    private val roomId: String
) : ViewModel() {
    private val _state = MutableStateFlow(PassengerBookingState())
    val state = _state.asStateFlow()

    init { refreshRoom() }

    fun refreshRoom() {
        if (_state.value.submitting) return
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val room = repository.getRoom(voyageId, roomId)
                _state.value = _state.value.copy(
                    loading = false, room = room,
                    error = if (room == null || !room.available || room.remainingCapacity <= 0)
                        "Phòng này không còn chỗ. Vui lòng chọn phòng khác." else null
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _state.value = _state.value.copy(loading = false, room = null, error = "Không tải được phòng. Kiểm tra mạng rồi thử lại.")
            }
        }
    }

    fun edit(draft: BookingDraft) {
        if (_state.value.submitting || _state.value.booking != null || _state.value.outcomeUnknown) return
        _state.value = _state.value.copy(draft = draft, error = null)
    }

    fun submit() {
        val current = _state.value
        if (current.loading || current.submitting || current.booking != null || current.outcomeUnknown) return
        val room = current.room ?: return
        val validation = current.draft.validate(if (room.available) room.remainingCapacity else 0)
        if (validation != null) {
            _state.value = current.copy(error = validation)
            return
        }
        _state.value = current.copy(submitting = true, error = null)
        viewModelScope.launch {
            try {
                // The server calculates the price and performs the final capacity check.
                val booking = repository.create(current.draft.toRequest(voyageId, roomId))
                _state.value = _state.value.copy(submitting = false, booking = booking)
            } catch (error: CancellationException) {
                throw error
            } catch (error: HttpException) {
                val unknown = error.code() >= 500 || error.code() == 408
                _state.value = _state.value.copy(
                    submitting = false, outcomeUnknown = unknown,
                    room = if (error.code() == 409) null else room,
                    error = when (error.code()) {
                        400, 422 -> "Thông tin đặt phòng không hợp lệ hoặc chuyến không còn nhận đặt chỗ. Kiểm tra lại thông tin."
                        401 -> "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
                        403 -> "Tài khoản không có quyền đặt phòng."
                        404 -> "Không tìm thấy chuyến hoặc phòng đã chọn."
                        409 -> "Số chỗ đã thay đổi. Hãy tải lại phòng trước khi xác nhận."
                        else -> "Chưa xác định được kết quả giữ chỗ. Kiểm tra mục Booking của tôi trên web trước khi tạo lại để tránh trùng."
                    }
                )
            } catch (error: Exception) {
                // A lost response does not prove that the server failed to create the booking.
                _state.value = _state.value.copy(
                    submitting = false, outcomeUnknown = true,
                    error = "Mất kết nối khi giữ chỗ. Kiểm tra Booking của tôi trên web trước khi tạo lại để tránh trùng."
                )
            }
        }
    }
}

class PassengerBookingViewModelFactory(
    private val repository: PassengerBookingRepository,
    private val voyageId: String,
    private val roomId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(PassengerBookingViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return PassengerBookingViewModel(repository, voyageId, roomId) as T
    }
}
