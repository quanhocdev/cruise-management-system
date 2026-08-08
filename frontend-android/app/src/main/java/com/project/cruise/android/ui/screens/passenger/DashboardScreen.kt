package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.MeState

@Composable
fun Dashboard(
    viewModel: AuthViewModel
) {

    val meState by viewModel.meState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Passenger Dashboard"
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {
                viewModel.getCurrentUser()
            },
            enabled = meState !is MeState.Loading
        ) {
            Text(
                text = if (meState is MeState.Loading) {
                    "Đang kiểm tra..."
                } else {
                    "Kiểm tra đăng nhập"
                }
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        when (val state = meState) {

            is MeState.Idle -> {
                Text("Chưa gọi API")
            }

            is MeState.Loading -> {
                Text("Đang gọi /api/auth/me...")
            }

            is MeState.Success -> {
                Text(
                    text = "Username: ${state.username}"
                )

                Text(
                    text = "Role: ${state.role}"
                )
            }

            is MeState.Error -> {
                Text(
                    text = "Lỗi: ${state.message}"
                )
            }
        }
    }
}
