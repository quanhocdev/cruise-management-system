package com.project.cruise.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.convenience.ProductTourResponse
import com.project.cruise.android.data.dto.convenience.ServiceTourResponse
import com.project.cruise.android.data.dto.convenience.ProductUsageResponse
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

    private val _uiState = MutableStateFlow(
        ConvenienceUsageUiState()
    )

    val uiState: StateFlow<ConvenienceUsageUiState> =
        _uiState.asStateFlow()

    fun loadConvenienceTours(tourId: String) {

        if (_uiState.value.isLoading) {
            return
        }

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null
        )

        viewModelScope.launch {

            runCatching {

                val products = repository.getProductTours(tourId)
                val services = repository.getServiceTours(tourId)

                Pair(products, services)
            }
                .onSuccess { (products, services) ->

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        products = products,
                        services = services,
                        errorMessage = null
                    )
                }
                .onFailure { exception ->

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                            ?: "Không thể tải danh sách tiện ích."
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

        if (quantity < 1) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Số lượng phải lớn hơn 0."
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            isSubmitting = true,
            errorMessage = null,
            successMessage = null,
            lastProductUsage = null
        )

        viewModelScope.launch {

            runCatching {
                repository.createProductUsage(
                    nfcCardUid = nfcCardUid,
                    productTourId = productTourId,
                    quantity = quantity
                )
            }
                .onSuccess { response ->

                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        lastProductUsage = response,
                        successMessage = "Đã ghi nhận sử dụng sản phẩm !",
                        errorMessage = null
                    )
                }
                .onFailure { exception ->

                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        errorMessage = exception.message
                            ?: "Không thể ghi nhận sản phẩm."
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

        _uiState.value = _uiState.value.copy(
            isSubmitting = true,
            errorMessage = null,
            successMessage = null,
            lastServiceUsage = null
        )

        viewModelScope.launch {

            runCatching {
                repository.createServiceUsage(
                    nfcCardUid = nfcCardUid,
                    serviceTourId = serviceTourId
                )
            }
                .onSuccess { response ->

                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        lastServiceUsage = response,
                        successMessage = "Đã ghi nhận sử dụng dịch vụ !",
                        errorMessage = null
                    )
                }
                .onFailure { exception ->

                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        errorMessage = exception.message
                            ?: "Không thể ghi nhận dịch vụ."
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
}
