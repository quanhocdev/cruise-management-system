package com.project.cruise.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.onboard.ActivityCruiseTourResponse
import com.project.cruise.android.data.dto.onboard.ActivityCruiseUsageResponse
import com.project.cruise.android.data.repository.OnboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardUsageUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val activities: List<ActivityCruiseTourResponse> = emptyList(),
    val selectedActivity: ActivityCruiseTourResponse? = null,
    val lastUsage: ActivityCruiseUsageResponse? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class OnboardUsageViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = OnboardRepository(application)

    private val _uiState = MutableStateFlow(OnboardUsageUiState())
    val uiState: StateFlow<OnboardUsageUiState> = _uiState.asStateFlow()

    fun loadActivities(tourId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val activities = repository.getActivityCruiseTours(tourId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    activities = activities,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                        ?: "Không thể tải hoạt động trên tàu"
                )
            }
        }
    }

    fun selectActivity(
        activity: ActivityCruiseTourResponse
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
        activityCruiseTourId: String
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
                    repository.createActivityCruiseUsage(
                        nfcCardUid = nfcCardUid,
                        activityCruiseTourId = activityCruiseTourId
                    )

                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    lastUsage = response,
                    successMessage =
                        "Đã ghi nhận tham gia hoạt động!",
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = e.message
                        ?: "Không thể ghi nhận tham gia hoạt động"
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
        _uiState.value = OnboardUsageUiState()
    }
}
