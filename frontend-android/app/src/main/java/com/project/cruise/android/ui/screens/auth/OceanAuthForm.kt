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
        OceanHeader(if (register) "Tạo tài khoản" else "Đăng nhập", onBack, !loading)
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            ShipEmblem(Modifier.size(64.dp))
            Spacer(Modifier.width(14.dp))
            Column {
                Text(if (register) "Bắt đầu hành trình" else "Chào mừng trở lại",
                    style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Cổng hành khách OceanCruise", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = MaterialTheme.shapes.large) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OceanInput(username, { username = it }, "Tên đăng nhập", !loading)
                if (register) OceanInput(email, { email = it }, "Email", !loading, KeyboardType.Email)
                OutlinedTextField(password, { password = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Mật khẩu") },
                    enabled = !loading, singleLine = true, shape = MaterialTheme.shapes.medium,
                    visualTransformation = if (reveal) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = { TextButton(onClick = { reveal = !reveal }, enabled = !loading) { Text(if (reveal) "Ẩn" else "Hiện") } },
                    colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = OceanPearl, focusedContainerColor = Color.White))
                error?.let { OceanNotice(it, error = true) }
                OceanPrimaryButton(if (register) "Đăng ký" else "Đăng nhập", { onSubmit(username.trim(), password, email.trim()) },
                    enabled = username.isNotBlank() && password.isNotBlank() && (!register || email.isNotBlank()), loading = loading)
            }
        }
        OceanNotice(if (register) "Sau khi đăng ký, mã OTP 6 số sẽ được gửi đến email của bạn."
            else "Đăng nhập bằng tài khoản hành khách đã đăng ký.")
    }
}

@Composable
internal fun OceanInput(value: String, onChange: (String) -> Unit, label: String, enabled: Boolean,
    keyboard: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(value, onChange, modifier = Modifier.fillMaxWidth(), label = { Text(label) },
        singleLine = true, enabled = enabled, shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = OceanPearl, focusedContainerColor = Color.White))
}
