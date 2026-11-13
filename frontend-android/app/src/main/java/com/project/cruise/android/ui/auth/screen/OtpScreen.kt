package com.project.cruise.android.ui.auth.screen

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.auth.components.otp.OtpVerificationPanel
import com.project.cruise.android.ui.theme.*

@Composable
fun OtpScreen(
    userId: String,
    onBackClick: () -> Unit,
    onVerify: (String, String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
) {
    var otp by remember(userId) { mutableStateOf("") }

    OceanPage {
        OceanHeader("Xác thực email", onBackClick, !isLoading)
        OtpVerificationPanel(
            heading = "Nhập mã OTP",
            description = "Mã gồm 6 chữ số đã được gửi đến email đăng ký.",
            otp = otp,
            onOtpChange = { otp = it },
            submitText = "Xác thực",
            onSubmit = { onVerify(userId, otp) },
            isLoading = isLoading,
            errorMessage = errorMessage,
            hint = "Kiểm tra cả thư mục spam nếu chưa thấy email xác thực.",
            emblem = { ShipEmblem(Modifier.size(80.dp)) },
        )
    }
}