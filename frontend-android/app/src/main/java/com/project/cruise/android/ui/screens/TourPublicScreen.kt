package com.project.cruise.android.ui.screens

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
import com.project.cruise.android.data.dto.tour.PublicTourSummaryResponse
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.tour.TourListState
import com.project.cruise.android.viewmodel.tour.TourViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TourPublicScreen(
    viewModel: TourViewModel,
    onTourClick: (String) -> Unit,
    onAuthClick: () -> Unit // Thay thế cho onBack
) {
    val state by viewModel.tourListState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchPublicTours()
    }

    OceanPage {
        // Sửa Header tại đây: Căn đều 2 bên (Tiêu đề bên trái, nút Tài khoản bên phải)
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Khám Phá Tour",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = OceanNavy
            )
            FilledTonalButton(
                onClick = onAuthClick,
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White)
            ) {
                Text("Tài khoản", color = OceanTeal, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))

        when (val currentState = state) {
            is TourListState.Idle,
            is TourListState.Loading -> {
                Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OceanTeal)
                }
            }
            is TourListState.Error -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Lỗi tải dữ liệu", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                        Text(currentState.message, color = MaterialTheme.colorScheme.onErrorContainer)
                        OutlinedButton(onClick = { viewModel.fetchPublicTours() }) {
                            Text("Thử lại")
                        }
                    }
                }
            }
            is TourListState.Success -> {
                val tours = currentState.tours
                if (tours.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                        Text("Hiện không có tour nào mở bán.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tours.forEach { tour ->
                            TourSummaryCard(tour = tour, onClick = { onTourClick(tour.id) })
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun TourSummaryCard(
    tour: PublicTourSummaryResponse,
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
                    text = tour.name ?: "Tour #${tour.code}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanTeal
                )
                Surface(
                    color = OceanMint.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = tour.statusBooking?.name ?: "OPEN",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = OceanNavy,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text("Tàu: ${tour.cruiseName ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
            Text("Thời gian: ${tour.startDate ?: ""} đến ${tour.endDate ?: ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            val formattedPrice = tour.startingPrice?.let {
                NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(it)
            } ?: "Liên hệ"

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Từ: $formattedPrice",
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy
                )
                Text(
                    text = "Xem chi tiết →",
                    color = OceanTeal,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}