package com.project.cruise.android.ui.pos.convenience.components.nfcscan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

enum class NfcScanStatus {
    UNSUPPORTED,
    DISABLED,
    RESOLVING,
    READY,
}

@Composable
fun NfcScanHeading(status: NfcScanStatus, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            "Quét NFC hành khách",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        val message =
            when (status) {
                NfcScanStatus.UNSUPPORTED -> "Điện thoại này không hỗ trợ NFC."
                NfcScanStatus.DISABLED ->
                    "NFC đang tắt. Hãy bật NFC trong Cài đặt rồi quay lại màn hình này."
                NfcScanStatus.RESOLVING -> "Đã nhận thẻ. Đang xác định hành khách..."
                NfcScanStatus.READY -> "Đưa thẻ NFC của hành khách sát mặt sau điện thoại."
            }
        val isProblem = status == NfcScanStatus.UNSUPPORTED || status == NfcScanStatus.DISABLED

        if (isProblem)
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    message,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        else
            Text(
                message,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
    }
}