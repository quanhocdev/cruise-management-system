package com.project.cruise.android.viewmodel.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.network.ConvenienceProductResponse
import com.project.cruise.android.data.network.ConvenienceServiceResponse
import com.project.cruise.android.data.repository.ConveniencePosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class ConveniencePosState(
    val loading: Boolean = true,
    val nfcUid: String? = null,
    val products: List<ConvenienceProductResponse> = emptyList(),
    val services: List<ConvenienceServiceResponse> = emptyList(),
    val error: String? = null
)

class ConveniencePosViewModel(
    private val repository: ConveniencePosRepository,
    private val localId: String
) : ViewModel() {
    private val _state = MutableStateFlow(ConveniencePosState())
    val state = _state.asStateFlow()

    init { reload() }

    fun reload() {
        viewModelScope.launch {
            val uid = runCatching { repository.readNfcUid(localId) }
                .getOrElse { error ->
                    _state.value = ConveniencePosState(
                        loading = false,
                        error = error.message ?: "Không đọc được UID đã lưu"
                    )
                    return@launch
                }
            _state.value = ConveniencePosState(loading = true, nfcUid = uid)
            _state.value = runCatching {
                repository.getProducts() to repository.getServices()
            }
                .fold(
                    onSuccess = { (products, services) -> ConveniencePosState(
                        loading = false,
                        nfcUid = uid,
                        products = products,
                        services = services
                    ) },
                    onFailure = { error -> ConveniencePosState(
                        loading = false,
                        nfcUid = uid,
                        error = error.toUserMessage()
                    ) }
                )
        }
    }
}

private fun Throwable.toUserMessage(): String = when (this) {
    is IOException -> "Không kết nối được máy chủ. Kiểm tra mạng rồi thử lại."
    is HttpException -> when (code()) {
        401 -> "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
        403 -> "Tài khoản không có quyền xem danh mục tiện ích."
        else -> "Máy chủ phản hồi lỗi HTTP ${code()}."
    }
    else -> message ?: "Không tải được dữ liệu tiện ích"
}

class ConveniencePosViewModelFactory(
    private val repository: ConveniencePosRepository,
    private val localId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(ConveniencePosViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return ConveniencePosViewModel(repository, localId) as T
    }
}
