package com.project.cruise.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
fun GuestScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit, onPosClick: () -> Unit) {
    OceanTheme {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(OceanNavy, OceanNavySoft, OceanTeal))
            ).safeDrawingPadding()
        ) {
            Column(
                Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    ShipEmblem(Modifier.size(56.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("OCEANCRUISE", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        Text("HÀNH TRÌNH TRONG TẦM TAY", color = OceanMint, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Spacer(Modifier.weight(1f))
                Surface(
                    color = Color.White.copy(alpha = .10f),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text("KHÁM PHÁ ĐẠI DƯƠNG", Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = OceanMint,
                        style = MaterialTheme.typography.labelMedium)
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    "Hành trình đáng nhớ bắt đầu từ đây",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Khám phá tour, chọn chuyến và quản lý kỳ nghỉ của bạn trong một ứng dụng.",
                    color = Color.White.copy(alpha = .82f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.weight(1f))
                Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = OceanNavy)) {
                    Text("Đăng nhập")
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                    Text("Tạo tài khoản")
                }
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = onPosClick, modifier = Modifier.heightIn(min = 48.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = OceanMint)) {
                    Text("Truy cập dành cho máy POS  →")
                }
            }
        }
    }
}
