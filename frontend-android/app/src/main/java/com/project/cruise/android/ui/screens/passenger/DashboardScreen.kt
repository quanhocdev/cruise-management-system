package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.MeState

@Composable
fun Dashboard(
    viewModel: AuthViewModel,
    onLogout: () -> Unit // 🟢 Thêm callback này để điều hướng về Login
) {
    val meState by viewModel.meState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCurrentUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Passenger Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        when (val state = meState) {
            is MeState.Idle -> {
                Text("Đang chuẩn bị thông tin tài khoản...")
            }

            is MeState.Loading -> {
                Text("Đang gọi /api/auth/me...")
            }

            is MeState.Success -> {
                Text(text = "Username: ${state.username}")
                Text(text = "Role: ${state.role}")
            }

            is MeState.Error -> {
                Text(
                    text = "Lỗi: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 🔴 NÚT ĐĂNG XUẤT
        Button(
            onClick = {
                // Gọi logout trong ViewModel và điều hướng về Login khi hoàn tất
                viewModel.logout {
                    onLogout()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(
                text = "Đăng xuất",
                color = Color.White
            )
        }
    }
}
