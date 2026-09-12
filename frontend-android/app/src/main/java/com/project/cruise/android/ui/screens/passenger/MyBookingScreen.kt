package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.booking.BookingResponse
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.BookingListState
import com.project.cruise.android.viewmodel.passenger.BookingViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MyBookingsScreen(
    viewModel: BookingViewModel,
    onBookingClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.bookingListState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchMyBookings()
    }

    OceanPage {
        // Header
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            FilledTonalButton(
                onClick = onBack,
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White)
            ) {
                Text("←", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.width(12.dp))
            Text("Booking của tôi", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = OceanNavy)
        }

        Spacer(Modifier.height(16.dp))

        when (val currentState = state) {
            is BookingListState.Idle,
            is BookingListState.Loading -> {
                Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OceanTeal)
                }
            }
            is BookingListState.Error -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Lỗi tải dữ liệu", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                        Text(currentState.message, color = MaterialTheme.colorScheme.onErrorContainer)
                        OutlinedButton(onClick = { viewModel.fetchMyBookings() }) {
                            Text("Thử lại")
                        }
                    }
                }
            }
            is BookingListState.Success -> {
                val bookings = currentState.bookings
                if (bookings.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                        Text("Bạn chưa có booking nào.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    // Dùng Column kết hợp forEach thay cho LazyColumn để chạy hoàn hảo bên trong OceanPage cuộn dọc
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        bookings.forEach { booking ->
                            BookingCard(booking = booking, onClick = { onBookingClick(booking.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: BookingResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.bookingCode ?: "Mã: #${booking.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanTeal
                )
                Surface(
                    color = OceanMint.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = booking.status?.name ?: "UNKNOWN",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = OceanNavy,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text("Người liên hệ: ${booking.primaryContactName ?: "N/A"} (${booking.primaryContactPhone ?: ""})")
            Text("Số hành khách: ${booking.numberPassengers ?: booking.bookingPassengers?.size ?: 0}")

            val formattedAmount = booking.totalAmount?.let {
                NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(it)
            } ?: "0 đ"

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tổng tiền: $formattedAmount",
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy
                )
                Text(
                    text = "Chi tiết →",
                    color = OceanTeal,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}