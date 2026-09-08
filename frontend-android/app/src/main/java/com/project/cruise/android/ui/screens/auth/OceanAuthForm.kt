package com.project.cruise.android.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
internal fun OceanAuthForm(register: Boolean, onBack: () -> Unit, onSubmit: (String, String, String) -> Unit,
    loading: Boolean, error: String?) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var reveal by remember { mutableStateOf(false) }
    OceanPage {
        OceanHeader(if (register) "Đăng ký tài khoản" else "Đăng nhập", onBack, !loading)
        OceanBanner("CỔNG HÀNH KHÁCH", if (register) "Tạo tài khoản mới" else "Cruise Management",
            if (register) "Bắt đầu hành trình của bạn" else null, dark = !register)
        if (!register) {
            Text("Chào mừng trở lại", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("Đăng nhập để quản lý và trải nghiệm hành trình của bạn.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        OceanInput(username, { username = it }, "Tên đăng nhập", !loading)
        if (register) OceanInput(email, { email = it }, "Email cá nhân", !loading, KeyboardType.Email)
        OutlinedTextField(password, { password = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Mật khẩu") },
            enabled = !loading, singleLine = true, shape = MaterialTheme.shapes.medium,
            visualTransformation = if (reveal) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = { TextButton(onClick = { reveal = !reveal }, enabled = !loading) { Text(if (reveal) "Ẩn" else "Hiện") } },
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White))
        error?.let { OceanNotice(it, error = true) }
        Button(onClick = { onSubmit(username, password, email) }, enabled = !loading,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
            if (loading) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            else Text(if (register) "Đăng ký  →" else "Đăng nhập  →")
        }
        OceanNotice(if (register) "Đăng ký thành công sẽ chuyển sang xác thực OTP qua email."
            else "Sử dụng tên đăng nhập và mật khẩu của tài khoản đã đăng ký.")
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
internal fun OceanInput(value: String, onChange: (String) -> Unit, label: String, enabled: Boolean,
    keyboard: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(value, onChange, modifier = Modifier.fillMaxWidth(), label = { Text(label) },
        singleLine = true, enabled = enabled, shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White))
}
