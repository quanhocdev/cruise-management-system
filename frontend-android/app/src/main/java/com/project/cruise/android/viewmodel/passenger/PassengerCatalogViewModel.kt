package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.passenger.*
import com.project.cruise.android.data.repository.PassengerCatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PassengerCatalogState(
    val loading: Boolean = false,
    val error: String? = null,
    val tours: List<TourSummary> = emptyList(),
    val detail: TourDetail? = null,
    val departures: List<Departure> = emptyList(),
    val rooms: List<AvailableRoom> = emptyList()
)

class PassengerCatalogViewModel(private val repository: PassengerCatalogRepository) : ViewModel() {
    private val _state = MutableStateFlow(PassengerCatalogState())
    val state: StateFlow<PassengerCatalogState> = _state

    fun loadTours() = load { copy(tours = repository.getOpenTours()) }

    fun loadTour(tourId: String) = load {
        copy(
            detail = repository.getTourDetail(tourId),
            departures = repository.getDepartures(tourId)
        )
    }

    fun loadRooms(voyageId: String) {
        _state.value = _state.value.copy(rooms = emptyList())
        load { copy(rooms = repository.getAvailableRooms(voyageId).filter { it.available }) }
    }

    private fun load(update: suspend PassengerCatalogState.() -> PassengerCatalogState) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { _state.value.update() }
                .onSuccess { _state.value = it.copy(loading = false) }
                .onFailure { _state.value = _state.value.copy(loading = false, error = it.message ?: "Không thể tải dữ liệu") }
        }
    }
}

class PassengerCatalogViewModelFactory(
    private val repository: PassengerCatalogRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        PassengerCatalogViewModel(repository) as T
}
