package com.project.cruise.android.viewmodel.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.network.ShoreVisitTourResponse
import com.project.cruise.android.data.repository.ShorePosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class ShoreVisitState(
    val loading: Boolean = true,
    val qrValue: String? = null,
    val visitTours: List<ShoreVisitTourResponse> = emptyList(),
    val error: String? = null
)

class ShorePosViewModel(
    private val repository: ShorePosRepository,
    private val localId: String
) : ViewModel() {
    private val _state = MutableStateFlow(ShoreVisitState())
    val state = _state.asStateFlow()

    init { reload() }

    fun reload() {
        viewModelScope.launch {
            val qr = runCatching { repository.readTicketQr(localId) }
                .getOrElse {
                    _state.value = ShoreVisitState(loading = false, error = it.message ?: "Không đọc được mã QR")
                    return@launch
                }
            _state.value = ShoreVisitState(loading = true, qrValue = qr)
            _state.value = runCatching { repository.getVisitTours() }
                .fold(
                    onSuccess = { ShoreVisitState(loading = false, qrValue = qr, visitTours = it) },
                    onFailure = {
                        ShoreVisitState(loading = false, qrValue = qr, error = it.shoreMessage())
                    }
                )
        }
    }
}

private fun Throwable.shoreMessage(): String = when (this) {
    is IOException -> "Không kết nối được máy chủ. Kiểm tra Docker và mạng rồi thử lại."
    is HttpException -> when (code()) {
        401 -> "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
        403 -> "Tài khoản không có quyền xem chuyến tham quan bờ."
        else -> "Máy chủ phản hồi lỗi HTTP ${code()}."
    }
    else -> message ?: "Không tải được chuyến tham quan"
}

class ShorePosViewModelFactory(
    private val repository: ShorePosRepository,
    private val localId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(ShorePosViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return ShorePosViewModel(repository, localId) as T
    }
}
