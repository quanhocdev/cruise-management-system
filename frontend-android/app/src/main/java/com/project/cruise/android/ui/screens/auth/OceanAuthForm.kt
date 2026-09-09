package com.project.cruise.android.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.project.cruise.android.R
import com.project.cruise.android.ui.theme.OceanMint
import com.project.cruise.android.ui.theme.OceanNotice
import com.project.cruise.android.ui.theme.OceanPearl
import com.project.cruise.android.ui.theme.OceanPrimaryButton
import com.project.cruise.android.ui.theme.OceanTeal
import com.project.cruise.android.ui.theme.OceanTheme

@Composable
internal fun OceanAuthForm(
    register: Boolean,
    onBack: () -> Unit,
    onSubmit: (String, String, String) -> Unit,
    loading: Boolean,
    error: String?
) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var reveal by remember { mutableStateOf(false) }

    OceanTheme {
        Surface(Modifier.fillMaxSize(), color = OceanPearl) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(Modifier.fillMaxWidth().height(300.dp)) {
                    Image(
                        painter = painterResource(R.drawable.ocean_welcome_hero),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, OceanPearl))))
                    FilledTonalButton(
                        onClick = onBack,
                        enabled = !loading,
                        modifier = Modifier.safeDrawingPadding().padding(20.dp).heightIn(min = 52.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White.copy(alpha = .88f))
                    ) { Text("←", style = MaterialTheme.typography.titleLarge) }
                    Column(Modifier.align(Alignment.BottomStart).padding(horizontal = 28.dp, vertical = 26.dp)) {
                        Text("HẢI TRÌNH DI SẢN", color = OceanTeal, style = MaterialTheme.typography.labelLarge)
                        Text(if (register) "Tạo tài khoản" else "Chào mừng trở lại",
                            style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Text(
                            if (register) "Đăng ký để bắt đầu hành trình của bạn"
                            else "Đăng nhập để xem hành trình và booking",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).offset(y = (-8).dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        error?.let { OceanNotice(it, error = true) }
                        OceanInput(username, { username = it }, "Tên đăng nhập", !loading)
                        if (register) OceanInput(email, { email = it }, "Email", !loading, KeyboardType.Email)
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Mật khẩu") },
                            enabled = !loading,
                            singleLine = true,
                            shape = RoundedCornerShape(18.dp),
                            visualTransformation = if (reveal) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                TextButton(onClick = { reveal = !reveal }, enabled = !loading) {
                                    Text(if (reveal) "Ẩn" else "Hiện")
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = OceanPearl,
                                focusedContainerColor = Color.White
                            )
                        )
                        OceanPrimaryButton(
                            text = if (register) "Tạo tài khoản   →" else "Đăng nhập   →",
                            onClick = { onSubmit(username.trim(), password, email.trim()) },
                            enabled = username.isNotBlank() && password.isNotBlank() && (!register || email.isNotBlank()),
                            loading = loading
                        )
                    }
                }
                Text(
                    if (register) "Mã OTP sẽ được gửi đến email sau khi đăng ký."
                    else "Sử dụng tài khoản hành khách đã đăng ký.",
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
internal fun OceanInput(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    keyboard: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = OceanPearl,
            focusedContainerColor = Color.White
        )
    )
}
