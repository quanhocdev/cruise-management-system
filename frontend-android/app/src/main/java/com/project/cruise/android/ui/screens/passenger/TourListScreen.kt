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
fun TourListScreen(viewModel: PassengerCatalogViewModel, onBack: () -> Unit, onTourClick: (String) -> Unit) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadTours() }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        TextButton(onClick = onBack) { Text("← Quay lại") }
        Text("Tour đang mở bán", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Chọn hành trình phù hợp với bạn", color = MaterialTheme.colorScheme.onSurfaceVariant)
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp)) }
        if (state.loading && state.tours.isEmpty()) Box(Modifier.fillMaxSize()) { CircularProgressIndicator() }
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 20.dp)) {
            items(state.tours, key = { it.id }) { tour ->
                Card(Modifier.fillMaxWidth().clickable { onTourClick(tour.id) }) {
                    Column(Modifier.padding(18.dp)) {
                        Text(tour.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(tour.cruiseName, color = MaterialTheme.colorScheme.primary)
                        Text("${tour.startDate} → ${tour.endDate}", modifier = Modifier.padding(top = 8.dp))
                        tour.description?.takeIf { it.isNotBlank() }?.let {
                            Text(it, maxLines = 3, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
            if (!state.loading && state.tours.isEmpty()) item { Text("Hiện chưa có tour đang mở bán.") }
        }
    }
}
