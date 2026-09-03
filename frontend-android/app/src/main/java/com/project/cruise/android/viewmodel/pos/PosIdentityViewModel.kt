package com.project.cruise.android.viewmodel.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.network.PosIdentityResponse
import com.project.cruise.android.data.repository.PosIdentityRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class PosIdentityState(val loading: Boolean = false, val result: PosIdentityResponse? = null, val error: String? = null)

class PosIdentityViewModel(private val repository: PosIdentityRepository, private val localId: String) : ViewModel() {
    private val _state = MutableStateFlow(PosIdentityState())
    val state = _state.asStateFlow()
    init { verify() }

    fun verify() {
        if (_state.value.loading) return
        _state.value = PosIdentityState(loading = true)
        viewModelScope.launch {
            try { _state.value = PosIdentityState(result = repository.identify(localId)) }
            catch (error: CancellationException) { throw error }
            catch (error: IOException) {
                _state.value = PosIdentityState(error = "Đã lưu bản ghi quét. Chưa xác minh được vì mất kết nối; thử lại khi có mạng.")
            } catch (error: HttpException) {
                _state.value = PosIdentityState(error = if (error.code() == 401 || error.code() == 403)
                    "Thiết bị chưa được cấp quyền hoặc key không hợp lệ. Liên hệ Admin."
                    else "Máy chủ chưa xác minh được. Không thực hiện check-in hoặc thanh toán; hãy thử lại.")
            } catch (error: IllegalStateException) {
                _state.value = PosIdentityState(error = error.message)
            } catch (error: Exception) {
                _state.value = PosIdentityState(error = "Chưa xác minh được bản ghi quét. Vui lòng thử lại.")
            }
        }
    }
}

class PosIdentityViewModelFactory(private val repository: PosIdentityRepository, private val localId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(PosIdentityViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return PosIdentityViewModel(repository, localId) as T
    }
}
