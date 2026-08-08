package com.project.cruise.android.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,

    onRegister: (
        username: String,
        password: String,
        email: String
    ) -> Unit,

    isLoading: Boolean = false,

    errorMessage: String? = null
) {

    var username by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // ==========================
        // BACK
        // ==========================

        TextButton(
            onClick = onBackClick,
            enabled = !isLoading
        ) {
            Text("← Quay lại")
        }

        // ==========================
        // TITLE
        // ==========================

        Text(
            text = "Đăng ký"
        )

        // ==========================
        // USERNAME
        // ==========================

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
            },
            label = {
                Text("Tài khoản")
            },
            enabled = !isLoading,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        )

        // ==========================
        // PASSWORD
        // ==========================

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text("Mật khẩu")
            },
            enabled = !isLoading,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        // ==========================
        // EMAIL
        // ==========================

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text("Email")
            },
            enabled = !isLoading,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        // ==========================
        // ERROR
        // ==========================

        if (errorMessage != null) {

            Text(
                text = errorMessage,
                modifier = Modifier
                    .padding(top = 12.dp)
            )
        }

        // ==========================
        // REGISTER BUTTON
        // ==========================

        Button(
            onClick = {
                onRegister(
                    username,
                    password,
                    email
                )
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {

            if (isLoading) {

                CircularProgressIndicator()

            } else {

                Text("Đăng ký")
            }
        }
    }
}
