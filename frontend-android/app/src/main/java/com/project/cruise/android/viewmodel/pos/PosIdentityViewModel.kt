package com.project.cruise.android.viewmodel.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.network.ScanResponse
import com.project.cruise.android.data.repository.PosIdentityRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class PosScanState(
    val loading: Boolean = false,
    val response: ScanResponse? = null,
    val error: String? = null
)

class PosIdentityViewModel(
    private val repository: PosIdentityRepository,
    private val localId: String
) : ViewModel() {
    private val _state = MutableStateFlow(PosScanState())
    val state = _state.asStateFlow()

    init {
        sendScan()
    }

    fun sendScan() {
        if (_state.value.loading) return
        _state.value = PosScanState(loading = true)
        viewModelScope.launch {
            try {
                val apiResponse = repository.scanAndNotify(localId)
                _state.value = PosScanState(
                    loading = false,
                    response = apiResponse,
                    error = if (apiResponse.success) null else apiResponse.message
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: IOException) {
                _state.value = PosScanState(
                    loading = false,
                    error = "Đã lưu bản ghi quét. Chưa gửi được lên máy chủ do mất kết nối; WorkManager sẽ tự đồng bộ khi có mạng."
                )
            } catch (error: HttpException) {
                _state.value = PosScanState(
                    loading = false,
                    error = "Máy chủ phản hồi lỗi (HTTP ${error.code()}). Vui lòng thử lại."
                )
            } catch (error: Exception) {
                _state.value = PosScanState(
                    loading = false,
                    error = "Không thể xử lý mã quét: ${error.message ?: "Lỗi không xác định"}"
                )
            }
        }
    }
}

class PosIdentityViewModelFactory(
    private val repository: PosIdentityRepository,
    private val localId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(PosIdentityViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return PosIdentityViewModel(repository, localId) as T
    }
}