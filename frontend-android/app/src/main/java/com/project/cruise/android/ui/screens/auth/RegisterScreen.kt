package com.project.cruise.android.ui.screens.auth
import androidx.compose.runtime.Composable

@Composable
fun RegisterScreen(onBackClick: () -> Unit, onRegister: (String, String, String) -> Unit,
    isLoading: Boolean = false, errorMessage: String? = null) {
    OceanAuthForm(true, onBackClick, onRegister, isLoading, errorMessage)
}
