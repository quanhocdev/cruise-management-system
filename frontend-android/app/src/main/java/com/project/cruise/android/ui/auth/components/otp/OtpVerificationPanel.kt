package com.project.cruise.android.ui.auth.components.otp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.guest.components.guestwelcome.EntranceAnimation
import com.project.cruise.android.ui.theme.*

/** Nội dung dùng chung cho màn nhập OTP (email thường và Google). */
@Composable
fun OtpVerificationPanel(
    heading: String,
    description: String,
    otp: String,
    onOtpChange: (String) -> Unit,
    submitText: String,
    onSubmit: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    hint: String,
    emblem: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        EntranceAnimation(delayMillis = 0) { emblem() }

        EntranceAnimation(delayMillis = 120) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    heading,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy,
                    textAlign = TextAlign.Center,
                )
                Text(
                    description,
                    color = OceanSlate,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
            }
        }

        EntranceAnimation(delayMillis = 240, modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            ) {
                Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    OtpCodeField(
                        value = otp,
                        onValueChange = onOtpChange,
                        enabled = !isLoading,
                        isError = errorMessage != null,
                    )
                    errorMessage?.let { OceanNotice(it, error = true) }
                    OceanPrimaryButton(
                        text = submitText,
                        onClick = onSubmit,
                        enabled = otp.length == 6 && !isLoading,
                        loading = isLoading,
                    )
                }
            }
        }

        EntranceAnimation(delayMillis = 360, modifier = Modifier.fillMaxWidth()) {
            OceanNotice(hint)
        }
    }
}