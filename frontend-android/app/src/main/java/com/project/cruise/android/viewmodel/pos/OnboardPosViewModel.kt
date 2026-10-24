package com.project.cruise.android.viewmodel.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.repository.OnboardPosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardTicketState(
    val loading: Boolean = true,
    val qrValue: String? = null,
    val error: String? = null
)

class OnboardPosViewModel(
    private val repository: OnboardPosRepository,
    private val localId: String
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardTicketState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = runCatching { repository.readTicketQr(localId) }
                .fold(
                    onSuccess = { OnboardTicketState(loading = false, qrValue = it) },
                    onFailure = { OnboardTicketState(loading = false, error = it.message ?: "Không đọc được mã vé") }
                )
        }
    }
}

class OnboardPosViewModelFactory(
    private val repository: OnboardPosRepository,
    private val localId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(OnboardPosViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return OnboardPosViewModel(repository, localId) as T
    }
}
