package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.PassengerCatalogState
import com.project.cruise.android.viewmodel.passenger.PassengerCatalogViewModel

@Composable
fun TourDetailScreen(tourId: String, viewModel: PassengerCatalogViewModel, onBack: () -> Unit, onDepartureClick: (String) -> Unit) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(tourId) { viewModel.loadTour(tourId) }
    TourDetailContent(tourId, state, onBack, onDepartureClick) { viewModel.loadTour(tourId) }
}

@Composable
internal fun TourDetailContent(tourId: String, state: PassengerCatalogState, onBack: () -> Unit,
    onDepartureClick: (String) -> Unit, retry: () -> Unit) {
    TourPage("Danh sách tour", onBack) {
        tourLoadingAndError(state.loading, state.error, retry)
        state.detail?.takeIf { it.id == tourId }?.let { tour ->
            item { OceanBanner("HÀNH TRÌNH CỦA BẠN", tour.name, tour.cruiseName) }
            item {
                TourCard {
                    Text("${tour.startDate} → ${tour.endDate}", color = OceanTeal, fontWeight = FontWeight.Bold)
                    tour.description?.takeIf { it.isNotBlank() }?.let { Text(it) }
                }
            }
            item { TourSection("Lịch trình khám phá") }
            items(tour.itinerary, key = { it.id }) { day ->
                TourCard {
                    Text("NGÀY ${day.dayNumber} · ${day.date}", color = OceanTeal, style = MaterialTheme.typography.labelLarge)
                    Text(day.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    day.description?.takeIf { it.isNotBlank() }?.let { Text(it) }
                }
            }
            if (tour.itinerary.isEmpty()) item { OceanNotice("Lịch trình chi tiết đang được cập nhật.") }
            item { TourSection("Chọn chuyến khởi hành") }
            items(state.departures, key = { it.voyageId }) { departure ->
                TourCard {
                    Text("${departure.departureDate} → ${departure.returnDate}", fontWeight = FontWeight.Bold)
                    Text(departure.cruiseName, color = OceanTeal)
                    Text("Sức chứa: ${departure.capacity} hành khách")
                    Button(onClick = { onDepartureClick(departure.voyageId) }, enabled = !state.loading,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Chọn chuyến · Xem phòng →") }
                }
            }
            if (!state.loading && state.error == null && state.departures.isEmpty()) item { OceanNotice("Chưa có chuyến khởi hành phù hợp.") }
        }
    }
}
