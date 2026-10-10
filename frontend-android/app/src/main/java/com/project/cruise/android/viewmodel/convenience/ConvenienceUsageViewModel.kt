package com.project.cruise.android.viewmodel.convenience

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.convenience.ProductTourResponse
import com.project.cruise.android.data.dto.convenience.ProductUsageResponse
import com.project.cruise.android.data.dto.convenience.ServiceTourResponse
import com.project.cruise.android.data.dto.convenience.ServiceUsageResponse
import com.project.cruise.android.data.repository.ConvenienceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConvenienceUsageUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,

    val products: List<ProductTourResponse> = emptyList(),
    val services: List<ServiceTourResponse> = emptyList(),

    val lastProductUsage: ProductUsageResponse? = null,
    val lastServiceUsage: ServiceUsageResponse? = null,

    val successMessage: String? = null,
    val errorMessage: String? = null
)

class ConvenienceUsageViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ConvenienceRepository(application)

    private val _uiState = MutableStateFlow(ConvenienceUsageUiState())
    val uiState: StateFlow<ConvenienceUsageUiState> = _uiState.asStateFlow()

    fun loadConvenienceTours(tourId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val products = repository.getProductTours(tourId)
                val services = repository.getServiceTours(tourId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    products = products,
                    services = services,
                    errorMessage = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage =
                        e.message ?: "Không thể tải tiện ích"
                )
            }
        }
    }

    fun createProductUsage(
        nfcCardUid: String,
        productTourId: String,
        quantity: Int
    ) {
        if (_uiState.value.isSubmitting) {
            return
        }

        if (quantity <= 0) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Số lượng phải lớn hơn 0"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSubmitting = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                val response = repository.createProductUsage(
                    nfcCardUid = nfcCardUid,
                    productTourId = productTourId,
                    quantity = quantity
                )

                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    lastProductUsage = response,
                    successMessage = "Đã ghi nhận sử dụng sản phẩm !",
                    errorMessage = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage =
                        e.message ?: "Không thể ghi nhận sử dụng sản phẩm"
                )
            }
        }
    }

    fun createServiceUsage(
        nfcCardUid: String,
        serviceTourId: String
    ) {
        if (_uiState.value.isSubmitting) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSubmitting = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                val response = repository.createServiceUsage(
                    nfcCardUid = nfcCardUid,
                    serviceTourId = serviceTourId
                )

                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    lastServiceUsage = response,
                    successMessage = "Đã ghi nhận sử dụng dịch vụ !",
                    errorMessage = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage =
                        e.message ?: "Không thể ghi nhận sử dụng dịch vụ"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(
            successMessage = null
        )
    }

    fun clearLastUsage() {
        _uiState.value = _uiState.value.copy(
            lastProductUsage = null,
            lastServiceUsage = null
        )
    }

    fun reset() {
        _uiState.value = ConvenienceUsageUiState()
    }
}
