package com.project.cruise.android.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.project.cruise.android.ui.screens.GuestScreen

@Preview(name = "Welcome · 360dp", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun WelcomePreview() { GuestScreen({}, {}, {}) }

@Preview(name = "Login · 360dp", widthDp = 360, heightDp = 800, showBackground = true)
@Preview(name = "Login · large text", widthDp = 360, heightDp = 800, fontScale = 1.5f, showBackground = true)
@Composable
private fun LoginPreview() { LoginScreen({}, { _, _ -> }) }

@Preview(name = "Register · error", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun RegisterPreview() { RegisterScreen({}, { _, _, _ -> }, errorMessage = "Email hoặc tên đăng nhập chưa hợp lệ.") }

@Preview(name = "OTP · loading", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun OtpPreview() { OtpScreen(1, {}, { _, _ -> }, isLoading = true) }
