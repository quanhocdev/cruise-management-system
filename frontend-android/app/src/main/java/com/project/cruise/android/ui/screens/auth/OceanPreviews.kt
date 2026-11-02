package com.project.cruise.android.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.project.cruise.android.ui.screens.guest.GuestScreen

@Preview(name = "Welcome Â· 360dp", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun WelcomePreview() { GuestScreen({}, {}, {}) }

@Preview(name = "Login Â· 360dp", widthDp = 360, heightDp = 800, showBackground = true)
@Preview(name = "Login Â· large text", widthDp = 360, heightDp = 800, fontScale = 1.5f, showBackground = true)
@Composable
private fun LoginPreview() { LoginScreen({}, { _, _ -> }) }

@Preview(name = "Register Â· error", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun RegisterPreview() { RegisterScreen({}, { _, _, _ -> }, errorMessage = "Email hoáº·c tÃªn Ä‘Äƒng nháº­p chÆ°a há»£p lá»‡.") }

@Preview(name = "OTP Â· loading", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun OtpPreview() { OtpScreen("00000000-0000-4000-8000-000000000001", {}, { _, _ -> }, isLoading = true) }
