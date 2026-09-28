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
    var password by remember { mutableStateOf("") }
    var revealPassword by rememberSaveable { mutableStateOf(false) }

    PosTheme {
        Surface(Modifier.fillMaxSize(), color = PosBackground) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(PosBackground)
                    .safeDrawingPadding()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(24.dp)
            ) {
                TextButton(onClick = onBackClick, enabled = !isLoading) {
                    Text("← Quay lại", color = PosInk)
                }
                Spacer(Modifier.height(32.dp))
                Text("CRUISE POS", color = OceanTeal, style = MaterialTheme.typography.labelLarge)
                Text(
                    "Đăng nhập nhân viên",
                    color = PosInk,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Đăng nhập để bắt đầu phiên làm việc của bạn.",
                    color = PosMuted,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 28.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                    "Bộ phận sử dụng POS",
                    modifier = Modifier.padding(top = 24.dp, bottom = 10.dp),
                    color = OceanNavy,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PosRole.entries.forEach { role ->
                        Surface(
                            color = Color.White.copy(alpha = .92f),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                role.title,
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
