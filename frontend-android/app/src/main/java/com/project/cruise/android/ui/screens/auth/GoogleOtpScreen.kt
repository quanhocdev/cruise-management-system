
package com.project.cruise.android.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.OceanHeader
import com.project.cruise.android.ui.theme.OceanNotice
import com.project.cruise.android.ui.theme.OceanPage
import com.project.cruise.android.ui.theme.OceanPrimaryButton
@Composable
fun GoogleOtpScreen(
    userId: Long,
    onBackClick: () -> Unit,
    onVerify: (String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var otp by remember(userId) {
        mutableStateOf("")
    }

    OceanPage {
        OceanHeader(
            title = "Xác thực tài khoản Google",
            onBack = onBackClick,
            enabled = !isLoading
        )

        Text(
            text = "Xác thực email",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Tài khoản Google mới cần xác minh email. " +
                    "Hãy nhập mã OTP được gửi đến email của bạn.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = androidx.compose.ui.graphics.Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = otp,
                    onValueChange = { input ->
                        otp = input.filter(Char::isDigit).take(6)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    label = {
                        Text("Mã OTP gồm 6 chữ số")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword
                    )
                )

                errorMessage?.let {
                    OceanNotice(it, error = true)
                }

                OceanPrimaryButton(
                    text = "Xác thực và đăng nhập",
                    onClick = {
                        onVerify(otp)
                    },
                    enabled = otp.length == 6 && !isLoading,
                    loading = isLoading
                )
            }
        }

        OceanNotice(
            "Nếu chưa nhận được mã, hãy kiểm tra hộp thư spam."
        )
    }
}
