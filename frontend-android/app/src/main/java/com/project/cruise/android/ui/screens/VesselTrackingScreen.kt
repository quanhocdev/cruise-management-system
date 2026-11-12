package com.project.cruise.android.ui.screen // hoặc package UI của bạn

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.VesselTrackingViewModel

@Composable
fun VesselTrackingScreen(
    viewModel: VesselTrackingViewModel,
    tourId: Long,
    token: String,
    context: Context
) {
    // Lấy trạng thái tracking từ ViewModel
    val isTracking by viewModel.isTrackingActive.collectAsState()

    // Khởi tạo bộ phóng sự kiện xin quyền vị trí
    val permission = Manifest.permission.ACCESS_FINE_LOCATION
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Nếu được cấp quyền -> Bắt đầu gửi GPS định kỳ 10 giây/lần
            viewModel.startTourTracking(tourId, token)
        } else {
            // Xử lý khi người dùng từ chối quyền
            println("Cần cấp quyền vị trí để chạy tính năng giám sát tàu!")
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hiển thị trạng thái hiện tại của tàu
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isTracking) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isTracking) "🚢 Tàu đang trong hành trình" else "⚓ Tàu đang neo đậu / Dừng",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isTracking) "Đang tự động gửi tọa độ GPS (10s/lần)" else "Chưa kích hoạt chia sẻ vị trí",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Nút bấm Bắt đầu / Kết thúc Tour
            if (!isTracking) {
                Button(
                    onClick = {
                        // Kiểm tra xem đã có quyền vị trí chưa trước khi start
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context, permission
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            viewModel.startTourTracking(tourId, token)
                        } else {
                            // Chưa có thì xin quyền ngay lập tức
                            permissionLauncher.launch(permission)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Bắt đầu Chuyến đi (Bật Tracking 10s)")
                }
            } else {
                Button(
                    onClick = {
                        viewModel.stopTourTracking()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Kết thúc Chuyến đi (Tắt Tracking)")
                }
            }
        }
    }
}