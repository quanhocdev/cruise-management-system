package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.passenger.tripdetail.BookedTourItemDto
import com.project.cruise.android.data.repository.passenger.PassengerTripDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PassengerBookedToursUiState(
    val loading: Boolean = false,
    val tours: List<BookedTourItemDto> = emptyList(),
    val error: String? = null
)

class PassengerBookedToursViewModel(
    private val repository: PassengerTripDetailRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PassengerBookedToursUiState(loading = true))
    val uiState: StateFlow<PassengerBookedToursUiState> = _uiState.asStateFlow()

    init {
        loadBookedTours()
    }

    fun loadBookedTours() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            try {
                val list = repository.getMyBookedTours()
                _uiState.update {
                    it.copy(
                        loading = false,
                        tours = list,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        tours = emptyList(),
                        error = e.message ?: "Không thể tải danh sách hành trình."
                    )
                }
            }
        }
    }

    class Factory(
        private val repository: PassengerTripDetailRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PassengerBookedToursViewModel(repository) as T
        }
    }
}