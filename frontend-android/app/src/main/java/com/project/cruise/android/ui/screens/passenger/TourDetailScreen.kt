package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.passenger.PassengerCatalogViewModel

@Composable
fun TourDetailScreen(
    tourId: String,
    viewModel: PassengerCatalogViewModel,
    onBack: () -> Unit,
    onDepartureClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(tourId) { viewModel.loadTour(tourId) }

    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp), contentPadding = PaddingValues(vertical = 16.dp)) {
        item { TextButton(onClick = onBack) { Text("← Danh sách tour") } }
        state.error?.let { message -> item { Text(message, color = MaterialTheme.colorScheme.error) } }
        if (state.loading && state.detail?.id != tourId) item { CircularProgressIndicator() }
        state.detail?.takeIf { it.id == tourId }?.let { tour ->
            item {
                Text(tour.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(tour.cruiseName, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                Text("${tour.startDate} → ${tour.endDate}", modifier = Modifier.padding(top = 8.dp))
                tour.description?.let { Text(it, modifier = Modifier.padding(top = 12.dp)) }
                Text("Lịch trình", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 24.dp))
            }
            items(tour.itinerary, key = { it.id }) { day ->
                ListItem(
                    headlineContent = { Text("Ngày ${day.dayNumber}: ${day.name}") },
                    supportingContent = { Text("${day.date}${day.description?.let { "\n$it" } ?: ""}") }
                )
                HorizontalDivider()
            }
            item {
                Text("Chuyến khởi hành", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
            }
            items(state.departures, key = { it.voyageId }) { departure ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onDepartureClick(departure.voyageId) }) {
                    Column(Modifier.padding(16.dp)) {
                        Text("${departure.departureDate} → ${departure.returnDate}", fontWeight = FontWeight.Bold)
                        Text("${departure.cruiseName} • ${departure.capacity} hành khách")
                        Text("Chọn chuyến và xem phòng", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }
            if (!state.loading && state.departures.isEmpty()) item { Text("Chưa có chuyến khởi hành phù hợp.") }
        }
    }
}
