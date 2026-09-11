package com.project.cruise.android.ui.screens.auth
import androidx.compose.runtime.Composable

@Composable
fun LoginScreen(onBackClick: () -> Unit, onLogin: (String, String) -> Unit,
    isLoading: Boolean = false, errorMessage: String? = null) {
    OceanAuthForm(false, onBackClick, { username, password, _ -> onLogin(username, password) }, isLoading, errorMessage)
}
