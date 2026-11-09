package com.project.cruise.android.ui.auth.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import com.project.cruise.android.ui.auth.components.otp.AuthIconBadge
import com.project.cruise.android.ui.auth.components.otp.OtpVerificationPanel
import com.project.cruise.android.ui.theme.*

@Composable
fun GoogleOtpScreen(
    userId: String,
    onBackClick: () -> Unit,
    onVerify: (String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
) {
    var otp by remember(userId) { mutableStateOf("") }

    OceanPage {
        OceanHeader(
            title = "Xác thực tài khoản Google",
            onBack = onBackClick,
            enabled = !isLoading,
        )
        OtpVerificationPanel(
            heading = "Xác thực email",
            description =
                "Tài khoản Google mới cần xác minh email. " +
                        "Hãy nhập mã OTP được gửi đến email của bạn.",
            otp = otp,
            onOtpChange = { otp = it },
            submitText = "Xác thực và đăng nhập",
            onSubmit = { onVerify(otp) },
            isLoading = isLoading,
            errorMessage = errorMessage,
            hint = "Nếu chưa nhận được mã, hãy kiểm tra hộp thư spam.",
            emblem = { AuthIconBadge(Icons.Filled.Email) },
        )
    }
}