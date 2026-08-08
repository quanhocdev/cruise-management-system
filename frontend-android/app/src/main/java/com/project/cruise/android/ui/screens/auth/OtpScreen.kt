package com.project.cruise.android.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun OtpScreen(
    userId: Long,
    onBackClick: () -> Unit,
    onVerify: (Long, String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {

    var otp by remember {
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
            text = "Xác thực email"
        )

        Text(
            text = "Nhập mã OTP 6 số đã được gửi đến email của bạn.",
            modifier = Modifier.padding(top = 8.dp)
        )

        // ==========================
        // OTP
        // ==========================

        OutlinedTextField(
            value = otp,
            onValueChange = {
                if (
                    it.length <= 6 &&
                    it.all { char -> char.isDigit() }
                ) {
                    otp = it
                }
            },
            label = {
                Text("Mã OTP")
            },
            enabled = !isLoading,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
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
        // VERIFY
        // ==========================

        Button(
            onClick = {
                onVerify(
                    userId,
                    otp
                )
            },
            enabled = otp.length == 6 && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {

            if (isLoading) {

                CircularProgressIndicator()

            } else {

                Text("Xác thực")
            }
        }
    }
}
