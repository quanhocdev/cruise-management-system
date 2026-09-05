package com.project.cruise.android.viewmodel.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.network.PosIdentityResponse
import com.project.cruise.android.data.network.PosCheckInResponse
import com.project.cruise.android.data.repository.PosIdentityRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class PosIdentityState(
    val loading: Boolean = false,
    val checkingIn: Boolean = false,
    val result: PosIdentityResponse? = null,
    val checkInResult: PosCheckInResponse? = null,
    val error: String? = null
)

class PosIdentityViewModel(private val repository: PosIdentityRepository, private val localId: String) : ViewModel() {
    private val _state = MutableStateFlow(PosIdentityState())
    val state = _state.asStateFlow()
    init { verify() }

    fun verify() {
        if (_state.value.loading || _state.value.checkingIn) return
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

    fun checkIn() {
        val current = _state.value
        if (current.loading || current.checkingIn || current.result?.status != "IDENTIFIED") return
        _state.value = current.copy(checkingIn = true, error = null)
        viewModelScope.launch {
            try {
                val checked = repository.checkIn(localId)
                _state.value = _state.value.copy(checkingIn = false, checkInResult = checked)
            } catch (error: CancellationException) { throw error }
            catch (error: IOException) {
                _state.value = _state.value.copy(checkingIn = false,
                    error = "Mất kết nối: check-in chưa được xác nhận. Không quét lại liên tục; hãy kết nối mạng rồi thử lại.")
            } catch (error: HttpException) {
                _state.value = _state.value.copy(checkingIn = false,
                    error = if (error.code() == 401 || error.code() == 403)
                        "Thiết bị chưa được cấp quyền hoặc key không hợp lệ. Liên hệ Admin."
                    else "Máy chủ chưa thực hiện được check-in. Vui lòng thử lại.")
            } catch (error: IllegalStateException) {
                _state.value = _state.value.copy(checkingIn = false, error = error.message)
            } catch (error: Exception) {
                _state.value = _state.value.copy(checkingIn = false, error = "Check-in thất bại. Vui lòng thử lại.")
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
