package com.project.cruise.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.nfc.NfcResolveResponse
import com.project.cruise.android.data.repository.OnboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardNfcScanUiState(
    val isResolving: Boolean = false,
    val nfcCardUid: String? = null,
    val resolved: NfcResolveResponse? = null,
    val error: String? = null
)

class OnboardNfcScanViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = OnboardRepository(application)

    private val _uiState = MutableStateFlow(OnboardNfcScanUiState())
    val uiState: StateFlow<OnboardNfcScanUiState> = _uiState.asStateFlow()

    fun resolveNfc(nfcCardUid: String) {
        if (_uiState.value.isResolving) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isResolving = true,
                nfcCardUid = nfcCardUid,
                error = null,
                resolved = null
            )

            try {
                val response = repository.resolveNfc(nfcCardUid)

                _uiState.value = _uiState.value.copy(
                    isResolving = false,
                    resolved = response,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isResolving = false,
                    error = e.message ?: "Không thể xác thực thẻ NFC"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            error = null
        )
    }

    fun reset() {
        _uiState.value = OnboardNfcScanUiState()
    }
}