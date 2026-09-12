package com.project.cruise.android.ui.screens

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
import com.project.cruise.android.data.dto.tour.PublicTourDetailResponse
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.tour.TourDetailState
import com.project.cruise.android.viewmodel.tour.TourViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TourDetailPublicScreen(
    tourId: String,
    viewModel: TourViewModel,
    isLoggedIn: Boolean,
    onBookClick: (String) -> Unit,
    onLoginRequired: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.tourDetailState.collectAsState()

    LaunchedEffect(tourId) {
        viewModel.fetchTourDetail(tourId)
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
            Text("Chi Tiết Tour", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = OceanNavy)
        }

        Spacer(Modifier.height(16.dp))

        when (val currentState = state) {
            is TourDetailState.Idle,
            is TourDetailState.Loading -> {
                Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OceanTeal)
                }
            }
            is TourDetailState.Error -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Lỗi tải thông tin", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                        Text(currentState.message, color = MaterialTheme.colorScheme.onErrorContainer)
                        OutlinedButton(onClick = { viewModel.fetchTourDetail(tourId) }) {
                            Text("Thử lại")
                        }
                    }
                }
            }
            is TourDetailState.Success -> {
                val tour = currentState.tour
                TourDetailContent(
                    tour = tour,
                    isLoggedIn = isLoggedIn,
                    onBookClick = onBookClick,
                    onLoginRequired = onLoginRequired
                )
            }
        }
    }
}

@Composable
fun TourDetailContent(
    tour: PublicTourDetailResponse,
    isLoggedIn: Boolean,
    onBookClick: (String) -> Unit,
    onLoginRequired: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = tour.name ?: "Tour #${tour.code}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OceanTeal
                )
                Text(tour.description ?: "Không có mô tả.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Divider(color = OceanPearl)
                Text("Tàu: ${tour.cruise?.name ?: "N/A"}", fontWeight = FontWeight.SemiBold)
                Text("Hành trình: ${tour.startDate ?: ""} đến ${tour.endDate ?: ""}")
                Text("Trạng thái: ${tour.statusBooking?.name ?: "N/A"}", fontWeight = FontWeight.Bold, color = OceanNavy)
            }
        }

        Text("Các gói tour", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OceanNavy)

        val packages = tour.packages.orEmpty()
        if (packages.isEmpty()) {
            Text("Không có gói tour khả dụng.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            packages.forEach { pkg ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(pkg.name ?: "Gói dịch vụ", fontWeight = FontWeight.Bold, color = OceanTeal)
                        Text(pkg.description ?: "")
                        val formattedPrice = pkg.price?.let {
                            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(it)
                        } ?: "0 đ"
                        Text("Giá: $formattedPrice", fontWeight = FontWeight.Bold, color = OceanNavy)

                        Button(
                            onClick = {
                                if (isLoggedIn) {
                                    onBookClick(pkg.id)
                                } else {
                                    onLoginRequired()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = OceanTeal)
                        ) {
                            Text("Đặt tour ngay")
                        }
                    }
                }
            }
        }
    }
}