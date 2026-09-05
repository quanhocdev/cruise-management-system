package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.passenger.PassengerBookingResponse
import com.project.cruise.android.data.repository.PassengerBookingRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class BookingHistoryState(
    val loading: Boolean = false,
    val bookings: List<PassengerBookingResponse> = emptyList(),
    val detail: PassengerBookingResponse? = null,
    val qrBytes: ByteArray? = null,
    val error: String? = null
)

class PassengerBookingHistoryViewModel(private val repository: PassengerBookingRepository) : ViewModel() {
    private val _state = MutableStateFlow(BookingHistoryState())
    val state = _state.asStateFlow()

    fun loadMine() = execute { _state.value = _state.value.copy(bookings = repository.getMine()) }

    fun loadDetail(id: Long) = execute {
        val booking = repository.get(id)
        _state.value = _state.value.copy(detail = booking, qrBytes = null)
        if (booking.status == "CONFIRMED") {
            runCatching { repository.getQr(id) }.onSuccess { bytes ->
                _state.value = _state.value.copy(qrBytes = bytes)
            }
        }
    }

    private fun execute(block: suspend () -> Unit) {
        if (_state.value.loading) return
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try { block(); _state.value = _state.value.copy(loading = false) }
            catch (error: CancellationException) { throw error }
            catch (error: HttpException) {
                _state.value = _state.value.copy(loading = false, error = when (error.code()) {
                    401 -> "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
                    403 -> "Bạn không có quyền xem booking này."
                    404 -> "Không tìm thấy booking."
                    else -> "Không tải được booking từ máy chủ."
                })
            } catch (error: Exception) {
                _state.value = _state.value.copy(loading = false, error = "Không tải được dữ liệu. Kiểm tra kết nối rồi thử lại.")
            }
        }
    }
}

class PassengerBookingHistoryViewModelFactory(private val repository: PassengerBookingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(PassengerBookingHistoryViewModel::class.java))
        @Suppress("UNCHECKED_CAST") return PassengerBookingHistoryViewModel(repository) as T
    }
}
