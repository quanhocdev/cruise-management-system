package com.project.cruise.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GuestScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Cruise Management"
        )

        Button(
            onClick = onLoginClick,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Đăng nhập")
        }

        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Đăng ký")
        }
    }
}