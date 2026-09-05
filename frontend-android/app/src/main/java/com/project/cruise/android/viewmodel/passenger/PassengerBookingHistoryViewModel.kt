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
    val creatingPayment: Boolean = false,
    val paymentUrl: String? = null,
    val awaitingPaymentReturn: Boolean = false,
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

    fun startVnPay() {
        val booking = _state.value.detail ?: return
        if (booking.status != "PENDING_PAYMENT" || _state.value.creatingPayment) return
        _state.value = _state.value.copy(creatingPayment = true, error = null)
        viewModelScope.launch {
            try {
                val payment = repository.createVnPayPayment(booking)
                val url = payment.paymentUrl?.takeIf { it.startsWith("https://sandbox.vnpayment.vn/") }
                    ?: error("Máy chủ không trả về URL VNPay Sandbox hợp lệ")
                _state.value = _state.value.copy(creatingPayment = false, paymentUrl = url,
                    awaitingPaymentReturn = true)
            } catch (error: CancellationException) { throw error }
            catch (error: HttpException) {
                _state.value = _state.value.copy(creatingPayment = false,
                    error = if (error.code() == 409) "Booking không còn ở trạng thái chờ thanh toán."
                    else "Không tạo được giao dịch VNPay. Vui lòng thử lại.")
            } catch (error: Exception) {
                _state.value = _state.value.copy(creatingPayment = false,
                    error = "Không mở được VNPay Sandbox. Kiểm tra kết nối rồi thử lại.")
            }
        }
    }

    fun paymentUrlOpened() { _state.value = _state.value.copy(paymentUrl = null) }

    fun refreshAfterPaymentReturn(id: Long) {
        if (!_state.value.awaitingPaymentReturn || _state.value.loading) return
        _state.value = _state.value.copy(awaitingPaymentReturn = false)
        loadDetail(id)
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
