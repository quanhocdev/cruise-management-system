package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.project.cruise.android.data.dto.booking.BookingPassengerInfoResponse
import com.project.cruise.android.data.dto.booking.BookingResponse
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.BookingDetailState
import com.project.cruise.android.viewmodel.passenger.BookingViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MyBookingDetailScreen(
    bookingId: Long,
    viewModel: BookingViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.bookingDetailState.collectAsState()

    LaunchedEffect(bookingId) {
        viewModel.fetchBookingDetail(bookingId)
    }

    OceanPage {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            FilledTonalButton(
                onClick = onBack,
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White)
            ) {
                Text("←", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "Chi tiết Booking",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = OceanNavy
            )
        }

        Spacer(Modifier.height(16.dp))

        when (val currentState = state) {
            is BookingDetailState.Idle,
            is BookingDetailState.Loading -> {
                Box(
                    Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OceanTeal)
                }
            }
            is BookingDetailState.Error -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Lỗi tải thông tin",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(currentState.message, color = MaterialTheme.colorScheme.onErrorContainer)
                        OutlinedButton(onClick = { viewModel.fetchBookingDetail(bookingId) }) {
                            Text("Thử lại")
                        }
                    }
                }
            }
            is BookingDetailState.Success -> {
                val booking = currentState.booking
                BookingDetailContent(booking = booking)
            }
        }
    }
}

@Composable
fun BookingDetailContent(booking: BookingResponse) {
    val formattedAmount = booking.totalAmount?.let {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(it)
    } ?: "0 đ"

    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = booking.bookingCode ?: "Mã: #${booking.id}",
                        style = MaterialTheme.typography.titleLarge,
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
                            style = MaterialTheme.typography.labelMedium,
                            color = OceanNavy,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Divider(color = OceanPearl)

                DetailRow("Người liên hệ:", booking.primaryContactName ?: "N/A")
                DetailRow("Số điện thoại:", booking.primaryContactPhone ?: "N/A")
                DetailRow("Email liên hệ:", booking.primaryContactEmail ?: "N/A")
                DetailRow("Số lượng khách:", "${booking.numberPassengers ?: 0} hành khách")
                DetailRow("Tổng thanh toán:", formattedAmount)
                DetailRow("Ngày tạo:", booking.createdAt ?: "N/A")
            }
        }

        Text(
            text = "Danh sách hành khách",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OceanNavy
        )

        val passengers = booking.bookingPassengers.orEmpty()
        if (passengers.isEmpty()) {
            Text("Không có thông tin hành khách.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                passengers.forEach { passenger ->
                    PassengerInfoCard(passenger = passenger)
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold, color = OceanNavy)
    }
}

@Composable
fun PassengerInfoCard(passenger: BookingPassengerInfoResponse) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = passenger.fullName ?: "Hành khách #${passenger.id ?: ""}",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                color = OceanTeal
            )
            Text("Giới tính: ${passenger.gender ?: "N/A"}")
            Text("Số điện thoại: ${passenger.phoneNumber ?: "N/A"}")
            Text("Email: ${passenger.email ?: "N/A"}")
            Text("Trạng thái Check-in: ${passenger.checkinStatus ?: "PENDING"}")
        }
    }
}