package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.MeState
import com.project.cruise.android.ui.theme.*

@Composable
fun Dashboard(viewModel: AuthViewModel, onBrowseTours: () -> Unit, onMyBookings: () -> Unit,
    notificationButton: @Composable () -> Unit, onLogout: () -> Unit) {
    val meState by viewModel.meState.collectAsState()
    LaunchedEffect(Unit) { viewModel.getCurrentUser() }
    OceanPage {
        OceanBanner("OCEANCRUISE", "Hành trình của bạn", "Khám phá đại dương theo cách riêng.", dark = true)
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("XIN CHÀO", style = MaterialTheme.typography.labelLarge, color = OceanTeal)
                when (val state = meState) {
                    is MeState.Success -> {
                        Text(state.username, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text(if (state.role == "PASSENGER" || state.role == "ROLE_PASSENGER") "HÀNH KHÁCH" else state.role,
                            style = MaterialTheme.typography.labelLarge, color = OceanTeal)
                    }
                    is MeState.Error -> OceanNotice(state.message, true)
                    else -> LinearProgressIndicator(Modifier.fillMaxWidth())
                }
                OceanNotice("Tour, booking và thông báo của bạn luôn được lấy từ dữ liệu hệ thống.")
            }
        }
        OceanAction("Khám phá tour", "Tìm hiểu những hành trình đang mở bán", "⌕", onBrowseTours)
        OceanAction("Booking của tôi", "Quản lý vé và chuyến đi đã đặt", "▤", onMyBookings)
        notificationButton()
        Spacer(Modifier.height(12.dp))
        FilledTonalButton(onClick = { viewModel.logout { onLogout() } }, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            colors = ButtonDefaults.filledTonalButtonColors(containerColor = OceanLavender, contentColor = OceanNavy)) { Text("Đăng xuất  →") }
        Text("Cruise Management · Hành khách", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
