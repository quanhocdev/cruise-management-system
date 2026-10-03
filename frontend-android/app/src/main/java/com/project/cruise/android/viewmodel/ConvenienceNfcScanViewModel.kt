package com.project.cruise.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.convenience.NfcResolveResponse
import com.project.cruise.android.data.repository.ConvenienceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConvenienceNfcScanUiState(
    val isResolving: Boolean = false,
    val nfcCardUid: String? = null,
    val resolved: NfcResolveResponse? = null,
    val error: String? = null
)

class ConvenienceNfcScanViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ConvenienceRepository(application)

    private val _uiState = MutableStateFlow(
        ConvenienceNfcScanUiState()
    )

    val uiState: StateFlow<ConvenienceNfcScanUiState> =
        _uiState.asStateFlow()

    fun resolveNfc(nfcCardUid: String) {

        if (_uiState.value.isResolving) {
            return
        }

        _uiState.value = _uiState.value.copy(
            isResolving = true,
            nfcCardUid = nfcCardUid,
            resolved = null,
            error = null
        )

        viewModelScope.launch {
            runCatching {
                repository.resolveNfc(nfcCardUid)
            }
                .onSuccess { response ->
                    _uiState.value = ConvenienceNfcScanUiState(
                        isResolving = false,
                        nfcCardUid = nfcCardUid,
                        resolved = response
                    )
                }
                .onFailure { exception ->
                    _uiState.value = ConvenienceNfcScanUiState(
                        isResolving = false,
                        nfcCardUid = nfcCardUid,
                        error = exception.message
                            ?: "Không thể xác định hành khách từ NFC."
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
        _uiState.value = ConvenienceNfcScanUiState()
    }
}