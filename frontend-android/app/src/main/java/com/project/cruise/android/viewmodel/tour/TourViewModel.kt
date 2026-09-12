package com.project.cruise.android.viewmodel.tour

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.tour.PublicTourDetailResponse
import com.project.cruise.android.data.dto.tour.PublicTourSummaryResponse
import com.project.cruise.android.data.repository.TourRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class TourListState {
    object Idle : TourListState()
    object Loading : TourListState()
    data class Success(val tours: List<PublicTourSummaryResponse>) : TourListState()
    data class Error(val message: String) : TourListState()
}

sealed class TourDetailState {
    object Idle : TourDetailState()
    object Loading : TourDetailState()
    data class Success(val tour: PublicTourDetailResponse) : TourDetailState()
    data class Error(val message: String) : TourDetailState()
}

class TourViewModel(
    private val repository: TourRepository
) : ViewModel() {

    private val _tourListState = MutableStateFlow<TourListState>(TourListState.Idle)
    val tourListState: StateFlow<TourListState> = _tourListState

    private val _tourDetailState = MutableStateFlow<TourDetailState>(TourDetailState.Idle)
    val tourDetailState: StateFlow<TourDetailState> = _tourDetailState

    fun fetchPublicTours() {
        viewModelScope.launch {
            _tourListState.value = TourListState.Loading
            try {
                val list = repository.getPublicTours()
                _tourListState.value = TourListState.Success(list)
            } catch (e: Exception) {
                _tourListState.value = TourListState.Error(e.message ?: "Tải danh sách tour thất bại")
            }
        }
    }

    fun fetchTourDetail(tourId: String) {
        viewModelScope.launch {
            _tourDetailState.value = TourDetailState.Loading
            try {
                val detail = repository.getPublicTourDetail(tourId)
                _tourDetailState.value = TourDetailState.Success(detail)
            } catch (e: Exception) {
                _tourDetailState.value = TourDetailState.Error(e.message ?: "Tải chi tiết tour thất bại")
            }
        }
    }
}