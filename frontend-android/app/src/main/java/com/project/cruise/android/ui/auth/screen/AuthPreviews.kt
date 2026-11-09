package com.project.cruise.android.ui.auth.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.project.cruise.android.ui.guest.screen.GuestScreen

@Preview(name = "Welcome · 360dp", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun WelcomePreview() {
    GuestScreen({}, {}, {})
}

@Preview(name = "Login · 360dp", widthDp = 360, heightDp = 800, showBackground = true)
@Preview(
    name = "Login · large text",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.5f,
    showBackground = true,
)
@Composable
private fun LoginPreview() {
    LoginScreen({}, { _, _ -> })
}

@Preview(name = "Register · error", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun RegisterPreview() {
    RegisterScreen({}, { _, _, _ -> }, errorMessage = "Email hoặc tên đăng nhập chưa hợp lệ.")
}

@Preview(name = "OTP · loading", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun OtpPreview() {
    OtpScreen("00000000-0000-4000-8000-000000000001", {}, { _, _ -> }, isLoading = true)
}

@Preview(name = "Google OTP · error", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun GoogleOtpPreview() {
    GoogleOtpScreen("00000000-0000-4000-8000-000000000001", {}, {}, errorMessage = "Mã OTP không đúng.")
}