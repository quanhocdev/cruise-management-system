package com.project.cruise.android.viewmodel.shore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.shore.ActivityVisitTourResponse
import com.project.cruise.android.data.dto.shore.ActivityVisitUsageResponse
import com.project.cruise.android.data.repository.ShoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ShoreUsageUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val activities: List<ActivityVisitTourResponse> = emptyList(),
    val selectedActivity: ActivityVisitTourResponse? = null,
    val lastUsage: ActivityVisitUsageResponse? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class ShoreUsageViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ShoreRepository(application)

    private val _uiState = MutableStateFlow(ShoreUsageUiState())
    val uiState: StateFlow<ShoreUsageUiState> = _uiState.asStateFlow()

    fun loadActivities(tourId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val activities =
                    repository.getActivityVisitTours(tourId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    activities = activities,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                        ?: "Không thể tải hoạt động tham quan"
                )
            }
        }
    }

    fun selectActivity(
        activity: ActivityVisitTourResponse
    ) {
        _uiState.value = _uiState.value.copy(
            selectedActivity = activity,
            errorMessage = null
        )
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(
            selectedActivity = null
        )
    }

    fun createUsage(
        nfcCardUid: String,
        visitTourId: String
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
                val response =
                    repository.createActivityVisitUsage(
                        nfcCardUid = nfcCardUid,
                        visitTourId = visitTourId
                    )

                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    lastUsage = response,
                    successMessage =
                        "Đã ghi nhận tham gia hoạt động tham quan!",
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = e.message
                        ?: "Không thể ghi nhận tham gia hoạt động tham quan"
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
            lastUsage = null
        )
    }

    fun reset() {
        _uiState.value = ShoreUsageUiState()
    }
}