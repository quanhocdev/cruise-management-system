//package com.project.cruise.android.ui.screens.passenger
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.project.cruise.android.ui.theme.*
//import com.project.cruise.android.viewmodel.passenger.PassengerCatalogState
//import com.project.cruise.android.viewmodel.passenger.PassengerCatalogViewModel
//import java.text.NumberFormat
//import java.util.Locale
//
//@Composable
//fun AvailableRoomsScreen(voyageId: String, viewModel: PassengerCatalogViewModel, onBack: () -> Unit, onSelectRoom: (String) -> Unit) {
//    val state by viewModel.state.collectAsState()
//    LaunchedEffect(voyageId) { viewModel.loadRooms(voyageId) }
//    AvailableRoomsContent(state, onBack, onSelectRoom) { viewModel.loadRooms(voyageId) }
//}
//
//@Composable
//internal fun AvailableRoomsContent(state: PassengerCatalogState, onBack: () -> Unit, onSelectRoom: (String) -> Unit, retry: () -> Unit) {
//    val currency = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")) }
//    TourPage("Chi tiết tour", onBack) {
//        item { OceanBanner("KHÔNG GIAN NGHỈ DƯỠNG", "Phòng còn trống", "Chọn căn phòng phù hợp cho hành trình của bạn.") }
//        tourLoadingAndError(state.loading, state.error, retry)
//        items(state.rooms, key = { it.roomId }) { room ->
//            TourCard {
//                Text("PHÒNG ${room.roomCode} · TẦNG ${room.deckNumber}", color = OceanTeal, style = MaterialTheme.typography.labelLarge)
//                Text(room.roomTypeName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
//                Text("Còn ${room.remainingCapacity}/${room.capacity} chỗ")
//                room.roomTypeDescription?.takeIf { it.isNotBlank() }?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
//                HorizontalDivider(color = OceanLavender)
//                Text("Giá phòng", style = MaterialTheme.typography.labelLarge)
//                Text(currency.format(room.price), style = MaterialTheme.typography.headlineSmall, color = OceanTeal, fontWeight = FontWeight.Bold)
//                OceanPrimaryButton("Chọn phòng", { onSelectRoom(room.roomId) },
//                    enabled = !state.loading && room.available && room.remainingCapacity > 0)
//            }
//        }
//        if (!state.loading && state.error == null && state.rooms.isEmpty()) item {
//            OceanStatePanel("Chưa có phòng phù hợp", "Chuyến này hiện không còn phòng trống.", "▣")
//        }
//        item { Text("Giá và chỗ trống được kiểm tra từ hệ thống đặt chỗ.", style = MaterialTheme.typography.bodySmall,
//            color = MaterialTheme.colorScheme.onSurfaceVariant) }
//    }
//}
