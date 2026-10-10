package com.project.cruise.android.ui.components.nfc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Giao diện quét NFC đơn giản, dùng chung cho Onboard và Shore.
 * Không phụ thuộc ViewModel cụ thể, chỉ nhận trạng thái và callback.
 */
@Composable
fun SimpleNfcScanContent(
    isResolving: Boolean,
    error: String?,
    onUidRead: (String) -> Unit,
    onClearError: () -> Unit,
    onBackClick: () -> Unit
) {
    val nfcAdapter = rememberNfcAdapter()

    NfcReaderEffect(
        adapter = nfcAdapter,
        enabled = !isResolving,
        onUidRead = onUidRead
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Quét NFC",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            nfcAdapter == null -> {
                Text(
                    text = "Thiết bị không hỗ trợ NFC.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            !nfcAdapter.isEnabled -> {
                Text(
                    text = "NFC đang bị tắt. Vui lòng bật NFC trên thiết bị.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            isResolving -> {
                CircularProgressIndicator()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Đang xác thực thẻ NFC...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            error != null -> {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onClearError) {
                    Text("Thử lại")
                }
            }

            else -> {
                Text(
                    text = "Đưa thẻ NFC của hành khách lại gần thiết bị.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onBackClick) {
            Text("Quay lại")
        }
    }
}