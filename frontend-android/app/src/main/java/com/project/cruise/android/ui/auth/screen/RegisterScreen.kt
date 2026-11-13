package com.project.cruise.android.ui.auth.screen

import androidx.compose.runtime.Composable

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegister: (String, String, String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
) {
    AuthFormScreen(
        register = true,
        onBack = onBackClick,
        onSubmit = onRegister,
        loading = isLoading,
        error = errorMessage,
    )
}