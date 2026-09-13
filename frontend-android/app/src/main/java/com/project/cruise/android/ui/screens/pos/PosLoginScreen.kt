package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
fun PosLoginScreen(
    onBackClick: () -> Unit,
    onLogin: (String, String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var revealPassword by rememberSaveable { mutableStateOf(false) }

    OceanTheme {
        Surface(Modifier.fillMaxSize(), color = Color(0xFFF5F8F8)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(OceanNavy, Color(0xFF124E59), Color(0xFFF5F8F8))
                        )
                    )
                    .safeDrawingPadding()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(24.dp)
            ) {
                TextButton(onClick = onBackClick, enabled = !isLoading) {
                    Text("← Quay lại", color = Color.White)
                }
                Spacer(Modifier.height(32.dp))
                Text("CRUISE POS", color = OceanMint, style = MaterialTheme.typography.labelLarge)
                Text(
                    "Đăng nhập nhân viên",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Dùng tài khoản được Admin cấp. Khu vực này không hỗ trợ đăng ký.",
                    color = Color.White.copy(alpha = .82f),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 28.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Column(
                        Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        errorMessage?.let { OceanNotice(it, error = true) }
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Tên đăng nhập") },
                            enabled = !isLoading,
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Mật khẩu") },
                            enabled = !isLoading,
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = if (revealPassword) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            trailingIcon = {
                                TextButton(
                                    onClick = { revealPassword = !revealPassword },
                                    enabled = !isLoading
                                ) { Text(if (revealPassword) "Ẩn" else "Hiện") }
                            }
                        )
                        OceanPrimaryButton(
                            text = "Đăng nhập máy POS   →",
                            onClick = { onLogin(username.trim(), password) },
                            enabled = username.isNotBlank() && password.isNotBlank(),
                            loading = isLoading
                        )
                    }
                }

                Text(
                    "Role được phép",
                    modifier = Modifier.padding(top = 24.dp, bottom = 10.dp),
                    color = OceanNavy,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PosRole.entries.forEach { role ->
                        Surface(
                            color = Color.White.copy(alpha = .92f),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                role.apiRole,
                                Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                color = OceanNavy,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
