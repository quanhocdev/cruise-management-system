//package com.project.cruise.android.ui.screens.passenger
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.window.Dialog
//import androidx.compose.ui.window.DialogProperties
//import com.project.cruise.android.viewmodel.passenger.BookingHistoryState
//import com.project.cruise.android.ui.theme.*
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//
//@Composable
//fun BookingItineraryDialog(state: BookingHistoryState, onRetry: () -> Unit, onClose: () -> Unit) {
//    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
//        TripSurface {
//            Column(Modifier.fillMaxSize().safeDrawingPadding().imePadding().padding(20.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                TripBackButton("← Chi tiết booking", onClose)
//                Text("Lịch trình chuyến đi", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = OceanNavy)
//                state.trip?.tourName?.let { Text(it, style = MaterialTheme.typography.titleMedium) }
//                val days = state.trip?.itinerary
//                when {
//                    state.loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
//                    state.error != null || state.tripError != null || days == null -> {
//                        OceanNotice("Chưa tải được lịch trình. Vui lòng thử lại.", true)
//                    }
//                    days.isEmpty() -> OceanNotice("Chưa có lịch trình được công bố cho chuyến này.")
//                    else -> LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                        items(days.sortedBy { it.dayNumber }, key = { it.id }) { day ->
//                            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
//                                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                                    Text("Ngày ${day.dayNumber} · ${tripDate(day.date)}", style = MaterialTheme.typography.labelLarge, color = OceanTeal, fontWeight = FontWeight.Bold)
//                                    Text(day.name, style = MaterialTheme.typography.titleMedium)
//                                    Text(day.description?.takeIf { it.isNotBlank() } ?: "Chưa có mô tả cho ngày này.")
//                                }
//                            }
//                        }
//                    }
//                }
//                OutlinedButton(onClick = onRetry, enabled = !state.loading, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Tải lại lịch trình") }
//            }
//        }
//    }
//}
