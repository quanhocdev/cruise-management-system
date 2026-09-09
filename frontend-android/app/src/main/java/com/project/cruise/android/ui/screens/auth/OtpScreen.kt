package com.project.cruise.android.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
fun OtpScreen(userId: Long, onBackClick: () -> Unit, onVerify: (Long, String) -> Unit,
    isLoading: Boolean = false, errorMessage: String? = null) {
    var otp by remember(userId) { mutableStateOf("") }
    OceanPage {
        OceanHeader("Xác thực email", onBackClick, !isLoading)
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { ShipEmblem(Modifier.size(80.dp)) }
        Text("Nhập mã OTP", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Mã gồm 6 chữ số đã được gửi đến email đăng ký.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Card(colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                OtpCodeField(otp, { otp = it }, enabled = !isLoading)
                errorMessage?.let { OceanNotice(it, true) }
                OceanPrimaryButton("Xác thực", { onVerify(userId, otp) }, enabled = otp.length == 6, loading = isLoading)
            }
        }
        OceanNotice("Kiểm tra cả thư mục spam nếu chưa thấy email xác thực.")
    }
}

@Composable
private fun OtpCodeField(value: String, onValueChange: (String) -> Unit, enabled: Boolean) {
    BasicTextField(
        value = value,
        onValueChange = { raw ->
            val digits = raw.filter(Char::isDigit).take(6)
            onValueChange(digits)
        },
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        cursorBrush = SolidColor(Color.Transparent),
        decorationBox = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(6) { index ->
                    val digit = value.getOrNull(index)?.toString().orEmpty()
                    Surface(
                        modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                        shape = MaterialTheme.shapes.small,
                        color = if (index == value.length && enabled) OceanMintSoft else OceanPearl,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (index == value.length && enabled) OceanTeal else OceanLine
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(digit, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    )
}
