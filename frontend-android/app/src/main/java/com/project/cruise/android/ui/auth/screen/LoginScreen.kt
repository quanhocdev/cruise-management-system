package com.project.cruise.android.ui.auth.screen

import androidx.compose.runtime.Composable
import com.project.cruise.android.BuildConfig
import com.project.cruise.android.ui.auth.google.rememberGoogleSignIn

@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onLogin: (String, String) -> Unit,
    onGoogleIdToken: (String) -> Unit = {},
    isLoading: Boolean = false,
    errorMessage: String? = null,
) {
    if (!BuildConfig.GOOGLE_SIGN_IN_ENABLED) {
        AuthFormScreen(
            register = false,
            onBack = onBackClick,
            onSubmit = { username, password, _ -> onLogin(username, password) },
            loading = isLoading,
            error = errorMessage,
        )
        return
    }

    val google = rememberGoogleSignIn(onGoogleIdToken)

    AuthFormScreen(
        register = false,
        onBack = onBackClick,
        onSubmit = { username, password, _ -> onLogin(username, password) },
        loading = isLoading || google.loading,
        error = errorMessage ?: google.error,
        onGoogleLogin = google.launch,
    )
}