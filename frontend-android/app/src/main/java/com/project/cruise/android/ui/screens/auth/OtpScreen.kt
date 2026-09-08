package com.project.cruise.android.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
fun OtpScreen(userId: Long, onBackClick: () -> Unit, onVerify: (Long, String) -> Unit,
    isLoading: Boolean = false, errorMessage: String? = null) {
    var otp by remember(userId) { mutableStateOf("") }
    OceanPage {
        OceanHeader("Xác thực email", onBackClick, !isLoading)
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { ShipEmblem(Modifier.size(88.dp)) }
        Text("Nhập mã xác thực", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Nhập mã OTP 6 số đã gửi đến email của bạn để hoàn tất kích hoạt tài khoản.")
        Card(colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                OceanInput(otp, { if (it.length <= 6 && it.all(Char::isDigit)) otp = it }, "Mã OTP xác thực", !isLoading, KeyboardType.Number)
                errorMessage?.let { OceanNotice(it, true) }
                Button(onClick = { onVerify(userId, otp) }, enabled = otp.length == 6 && !isLoading,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                    if (isLoading) CircularProgressIndicator(Modifier.size(22.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    else Text("Xác thực  ✓")
                }
            }
        }
        OceanNotice("Kiểm tra cả thư mục spam nếu chưa thấy email xác thực.")
    }
}
