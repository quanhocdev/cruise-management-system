package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.OceanAction
import com.project.cruise.android.ui.theme.OceanHeader
import com.project.cruise.android.ui.theme.OceanLavender
import com.project.cruise.android.ui.theme.OceanMint
import com.project.cruise.android.ui.theme.OceanNavy
import com.project.cruise.android.ui.theme.OceanNotice
import com.project.cruise.android.ui.theme.OceanPage
import com.project.cruise.android.ui.theme.OceanTeal
import com.project.cruise.android.ui.theme.ShipEmblem
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.MeState
import com.project.cruise.android.viewmodel.passenger.BookingHistoryState
import com.project.cruise.android.R

@Composable
fun Dashboard(
    viewModel: AuthViewModel,
    bookingState: BookingHistoryState,
    onRefreshBookings: () -> Unit,
    onBrowseTours: () -> Unit,
    onMyBookings: () -> Unit,
    notificationButton: @Composable () -> Unit,
    onLogout: () -> Unit
) {
    val meState by viewModel.meState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getCurrentUser()
        onRefreshBookings()
    }

    OceanPage {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            ShipEmblem(Modifier.size(52.dp))
            Spacer(Modifier.size(12.dp))
            Text("OceanCruise", style = MaterialTheme.typography.titleLarge, color = OceanTeal,
                modifier = Modifier.weight(1f))
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Xin chào,", style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            when (val state = meState) {
                is MeState.Success -> {
                    Text(state.username, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text(if (state.role == "PASSENGER" || state.role == "ROLE_PASSENGER") "Hành khách"
                        else state.role, color = OceanTeal, style = MaterialTheme.typography.labelLarge)
                }
                is MeState.Error -> OceanNotice(state.message, true)
                else -> Text("Đang tải tài khoản…", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Card(shape = RoundedCornerShape(26.dp)) {
            Box(Modifier.fillMaxWidth().height(300.dp)) {
                Image(painterResource(R.drawable.ocean_welcome_hero), null, Modifier.fillMaxWidth().height(300.dp),
                    contentScale = ContentScale.Crop)
                Box(Modifier.fillMaxWidth().height(300.dp).background(
                    Brush.verticalGradient(listOf(Color.Transparent, OceanNavy.copy(alpha = .96f)))))
                Column(Modifier.align(Alignment.BottomStart).padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val latest = bookingState.bookings.firstOrNull()
                    Text(if (latest == null) "KHÁM PHÁ HÀNH TRÌNH" else "BOOKING GẦN NHẤT",
                        color = OceanMint, style = MaterialTheme.typography.labelLarge)
                    Text(latest?.bookingCode ?: "Du ngoạn giữa lòng đại dương", color = Color.White,
                        style = MaterialTheme.typography.headlineSmall)
                    Text(if (latest == null) "Chọn chuyến khởi hành và căn phòng phù hợp."
                        else "${bookingStatus(latest.status)} · ${latest.passengers.size} hành khách",
                        color = Color.White.copy(alpha = .84f))
                    FilledTonalButton(onClick = if (latest == null) onBrowseTours else onMyBookings,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = OceanMint, contentColor = OceanNavy)) {
                        Text(if (latest == null) "Khám phá ngay   →" else "Xem booking   →")
                    }
                }
            }
        }

        Text("Dịch vụ nhanh", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        OceanAction("Khám phá tour", "Xem tour, chuyến khởi hành và phòng", "⌕", onBrowseTours)
        OceanAction("Booking của tôi", "Thanh toán, QR và thông tin chuyến đi", "▤", onMyBookings)
        notificationButton()

        Box(Modifier.fillMaxWidth().background(OceanLavender, RoundedCornerShape(22.dp)).padding(18.dp)) {
            Text("Dữ liệu hiển thị được đồng bộ trực tiếp từ hệ thống.",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        FilledTonalButton(
            onClick = { viewModel.logout { onLogout() } },
            modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp),
            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White, contentColor = OceanNavy)
        ) { Text("Đăng xuất") }
        Spacer(Modifier.height(8.dp))
    }
}
