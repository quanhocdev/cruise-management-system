package com.project.cruise.android.ui.pos.convenience.components.nfcscan

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.pos.convenience.components.ConvenienceAccent

@Composable
fun NfcScanCaption(isResolving: Boolean, modifier: Modifier = Modifier) {
    Text(
        if (isResolving) "Đang kiểm tra thẻ..." else "Giữ thẻ ổn định trong giây lát",
        modifier = modifier,
        fontWeight = FontWeight.SemiBold,
        color = ConvenienceAccent,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun NfcSettingsButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = onClick, modifier = modifier) { Text("Mở cài đặt NFC") }
}

@Composable
fun NfcScanError(message: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
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
}

@Composable
fun NfcScanFooterNote(modifier: Modifier = Modifier) {
    Text(
        "NFC được dùng để xác định hành khách. Sau đó nhân viên sẽ chọn sản phẩm hoặc dịch vụ tiện ích.",
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}