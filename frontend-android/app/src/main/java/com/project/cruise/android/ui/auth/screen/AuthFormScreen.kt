package com.project.cruise.android.ui.auth.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.auth.components.authform.*
import com.project.cruise.android.ui.guest.components.guestwelcome.EntranceAnimation
import com.project.cruise.android.ui.theme.*

/** Form dùng chung cho đăng nhập và đăng ký. */
@Composable
internal fun AuthFormScreen(
    register: Boolean,
    onBack: () -> Unit,
    onSubmit: (String, String, String) -> Unit,
    loading: Boolean,
    error: String?,
    onGoogleLogin: (() -> Unit)? = null,
) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var reveal by remember { mutableStateOf(false) }

    val canSubmit =
        username.isNotBlank() && password.isNotBlank() && (!register || email.isNotBlank())
    val submit = {
        if (canSubmit && !loading) onSubmit(username.trim(), password, email.trim())
    }

    OceanTheme {
        Surface(Modifier.fillMaxSize(), color = OceanPearl) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AuthHero(register = register, onBack = onBack, backEnabled = !loading)

                EntranceAnimation(
                    delayMillis = 150,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).offset(y = (-8).dp),
                ) {
                    AuthFormCard {
                        error?.let { OceanNotice(it, error = true) }
                        AuthTextField(
                            value = username,
                            onChange = { username = it },
                            label = "Tên đăng nhập",
                            leadingIcon = Icons.Filled.Person,
                            enabled = !loading,
                        )
                        if (register)
                            AuthTextField(
                                value = email,
                                onChange = { email = it },
                                label = "Email",
                                leadingIcon = Icons.Filled.Email,
                                enabled = !loading,
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                            )
                        AuthPasswordField(
                            value = password,
                            onChange = { password = it },
                            revealed = reveal,
                            onToggleReveal = { reveal = !reveal },
                            enabled = !loading,
                            onDone = { submit() },
                        )
                        OceanPrimaryButton(
                            text = if (register) "Tạo tài khoản   →" else "Đăng nhập   →",
                            onClick = { submit() },
                            enabled = canSubmit,
                            loading = loading,
                        )
                        if (!register && onGoogleLogin != null) {
                            AuthDivider()
                            GoogleSignInButton(onClick = onGoogleLogin, enabled = !loading)
                        }
                    }
                }

                EntranceAnimation(delayMillis = 300, modifier = Modifier.fillMaxWidth()) {
                    AuthFooterNote(
                        if (register) "Mã OTP sẽ được gửi đến email sau khi đăng ký."
                        else "Hành khách dùng tài khoản đã đăng ký. Nhân viên dùng tài khoản được cấp."
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}