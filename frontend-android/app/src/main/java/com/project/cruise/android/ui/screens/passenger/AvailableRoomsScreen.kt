package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.passenger.PassengerCatalogViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AvailableRoomsScreen(voyageId: String, viewModel: PassengerCatalogViewModel, onBack: () -> Unit, onSelectRoom: (String) -> Unit) {
    val state by viewModel.state.collectAsState()
    val currency = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")) }
    LaunchedEffect(voyageId) { viewModel.loadRooms(voyageId) }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        TextButton(onClick = onBack) { Text("← Chi tiết tour") }
        Text("Phòng còn trống", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Giá và sức chứa được kiểm tra trực tiếp từ Booking Service", color = MaterialTheme.colorScheme.onSurfaceVariant)
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp)) }
        if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 20.dp)) {
            items(state.rooms, key = { it.roomId }) { room ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp)) {
                        Text(room.roomTypeName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Phòng ${room.roomCode} • Tầng ${room.deckNumber}")
                        Text("Còn ${room.remainingCapacity}/${room.capacity} chỗ", modifier = Modifier.padding(top = 6.dp))
                        Text(currency.format(room.price), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        room.roomTypeDescription?.let { Text(it, modifier = Modifier.padding(top = 6.dp)) }
                        Button(onClick = { onSelectRoom(room.roomId) }, enabled = !state.loading && room.available && room.remainingCapacity > 0) {
                            Text("Chọn phòng")
                        }
                    }
                }
            }
            if (!state.loading && state.rooms.isEmpty()) item { Text("Chuyến này hiện không còn phòng trống.") }
        }
    }
}
