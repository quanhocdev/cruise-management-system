package com.project.cruise.android.ui.screens.pos

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.nfc.NfcAdapter
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.local.pos.PosScanType
import com.project.cruise.android.data.repository.PosTransactionQueue
import kotlinx.coroutines.launch

@Composable
fun NfcScanScreen(onBackClick: () -> Unit, onSaved: () -> Unit) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val adapter = remember(context) { NfcAdapter.getDefaultAdapter(context) }
    val queue = remember { PosTransactionQueue(context) }
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    DisposableEffect(activity, adapter) {
        if (activity != null && adapter != null && adapter.isEnabled) {
            adapter.enableReaderMode(
                activity,
                { tag ->
                    if (!isSaving) {
                        val uid = tag.id.joinToString("") { byte -> "%02X".format(byte.toInt() and 0xFF) }
                        isSaving = true
                        scope.launch {
                            runCatching { queue.enqueue(PosScanType.NFC, uid) }
                                .onSuccess { onSaved() }
                                .onFailure {
                                    error = "Không thể lưu giao dịch trên thiết bị"
                                    isSaving = false
                                }
                        }
                    }
                },
                NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or NfcAdapter.FLAG_READER_NFC_V or
                    NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                null
            )
        }
        onDispose { if (activity != null && adapter != null) adapter.disableReaderMode(activity) }
    }

    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = onBackClick, modifier = Modifier.align(Alignment.Start), enabled = !isSaving) {
            Text("← Quay lại POS")
        }
        Spacer(Modifier.weight(1f))
        Text("Quét thẻ NFC", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            when {
                adapter == null -> "Điện thoại này không hỗ trợ NFC."
                !adapter.isEnabled -> "NFC đang tắt. Hãy bật NFC trong Cài đặt rồi quay lại màn hình này."
                else -> "Đưa thẻ hoặc vòng đeo tay NFC sát mặt sau điện thoại."
            },
            modifier = Modifier.padding(top = 12.dp),
            color = if (adapter == null || !adapter.isEnabled) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Box(
            Modifier.padding(top = 40.dp).background(Color(0xFFDDF3F0), CircleShape).padding(56.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isSaving) CircularProgressIndicator()
            else Text("NFC", color = Color(0xFF126A70), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        }
        Text(
            if (isSaving) "Đã nhận thẻ, đang lưu giao dịch..." else "Giữ thẻ ổn định trong giây lát",
            modifier = Modifier.padding(top = 24.dp),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        if (adapter != null && !adapter.isEnabled) {
            TextButton(onClick = { context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS)) }) {
                Text("Mở cài đặt NFC")
            }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp)) }
        Spacer(Modifier.weight(1f))
        Text(
            "UID thẻ được lưu cục bộ trước và chỉ có trạng thái đồng bộ sau khi backend phản hồi.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
