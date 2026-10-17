package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.booking.BookingResponse
import com.project.cruise.android.data.dto.tour.PublicTourDetailResponse
import com.project.cruise.android.data.repository.PassengerBookingRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class PassengerBookingState(
    val loading: Boolean = true,
    val submitting: Boolean = false,
    val tourPackage: PublicTourDetailResponse.TourPackageRecord? = null,
    val draft: BookingDraft = BookingDraft(),
    val error: String? = null,
    val outcomeUnknown: Boolean = false,
    val booking: BookingResponse? = null
)

class PassengerBookingViewModel(
    private val repository: PassengerBookingRepository,
    private val tourId: String,
    private val packageId: String
) : ViewModel() {
    private val _state = MutableStateFlow(PassengerBookingState())
    val state = _state.asStateFlow()

    init { refreshPackage() }

    fun refreshPackage() {
        if (_state.value.submitting) return
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val pkg = repository.getPackage(tourId, packageId)
                _state.value = _state.value.copy(
                    loading = false,
                    tourPackage = pkg,
                    error = if (pkg == null) "Gói tour không còn tồn tại. Vui lòng chọn lại." else null
                )
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _state.value = _state.value.copy(loading = false, error = "Không tải được gói tour. Kiểm tra kết nối rồi thử lại.")
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
        val pkg = current.tourPackage ?: return
        current.draft.validate(pkg.maxPassengers)?.let {
            _state.value = current.copy(error = it)
            return
        }
        _state.value = current.copy(submitting = true, error = null)
        viewModelScope.launch {
            try {
                val booking = repository.create(tourId, packageId, current.draft)
                _state.value = _state.value.copy(submitting = false, booking = booking)
            } catch (error: CancellationException) {
                throw error
            } catch (error: HttpException) {
                _state.value = _state.value.copy(
                    submitting = false,
                    outcomeUnknown = error.code() >= 500 || error.code() == 408,
                    error = when (error.code()) {
                        400, 422 -> "Thông tin chưa hợp lệ hoặc gói tour không còn đủ phòng. Vui lòng kiểm tra lại."
                        401 -> "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
                        403 -> "Tài khoản không có quyền đặt tour."
                        404 -> "Không tìm thấy tour hoặc gói tour đã chọn."
                        409 -> "Số phòng vừa thay đổi. Vui lòng tải lại trước khi xác nhận."
                        else -> "Chưa xác định được kết quả đặt tour. Hãy kiểm tra Booking của tôi để tránh tạo trùng."
                    }
                )
            } catch (_: Exception) {
                _state.value = _state.value.copy(
                    submitting = false,
                    outcomeUnknown = true,
                    error = "Mất kết nối khi đặt tour. Hãy kiểm tra Booking của tôi trước khi thử lại."
                )
            }
        }
    }
}

class PassengerBookingViewModelFactory(
    private val repository: PassengerBookingRepository,
    private val tourId: String,
    private val packageId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(PassengerBookingViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return PassengerBookingViewModel(repository, tourId, packageId) as T
    }
}
