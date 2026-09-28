package com.project.cruise.android.ui.screens.pos

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.nfc.NfcAdapter
import android.provider.Settings
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.project.cruise.android.BuildConfig
import com.project.cruise.android.data.local.pos.PosScanType
import com.project.cruise.android.data.repository.PosTransactionQueue
import kotlinx.coroutines.launch

@Composable
fun NfcScanScreen(
    role: PosRole,
    onBackClick: () -> Unit,
    onSaved: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val adapter = remember(context) { NfcAdapter.getDefaultAdapter(context) }
    val queue = remember { PosTransactionQueue(context) }
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var debugUid by remember { mutableStateOf("") }

    fun saveUid(uid: String) {
        val normalizedUid = uid.trim().uppercase()
        if (normalizedUid.isBlank()) return
        scope.launch {
            if (isSaving) return@launch
            isSaving = true
            error = null
            runCatching {
                queue.enqueue(
                    scanType = PosScanType.NFC,
                    scannedValue = normalizedUid,
                    operatorRole = role.apiRole,
                    operation = role.scanOperation
                )
            }
                .onSuccess(onSaved)
                .onFailure {
                    error = "Không thể lưu lượt đọc NFC trên thiết bị"
                    isSaving = false
                }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    var nfcEnabled by remember { mutableStateOf(adapter?.isEnabled == true) }
    var resumed by remember { mutableStateOf(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) }
    DisposableEffect(lifecycleOwner, adapter) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                nfcEnabled = adapter?.isEnabled == true
                resumed = true
            } else if (event == Lifecycle.Event.ON_PAUSE) resumed = false
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    DisposableEffect(activity, adapter, nfcEnabled, resumed) {
        if (activity != null && adapter != null && nfcEnabled && resumed) {
            adapter.enableReaderMode(
                activity,
                { tag ->
                    val uid = tag.id.joinToString("") { byte ->
                        "%02X".format(byte.toInt() and 0xFF)
                    }
                    saveUid(uid)
                },
                NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or NfcAdapter.FLAG_READER_NFC_V or
                    NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                null
            )
        }
        onDispose { if (activity != null && adapter != null) adapter.disableReaderMode(activity) }
    }

    PosTheme {
    Column(Modifier.fillMaxSize().background(PosBackground).safeDrawingPadding().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = onBackClick, modifier = Modifier.align(Alignment.Start), enabled = !isSaving) {
            Text("← Quay lại POS")
        }
        Spacer(Modifier.height(24.dp))
        PosBadge(role.title, role.accent())
        Spacer(Modifier.height(20.dp))
        Text("Đọc vòng NFC", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            when {
                adapter == null -> "Điện thoại này không hỗ trợ NFC."
                !nfcEnabled -> "NFC đang tắt. Hãy bật NFC trong Cài đặt rồi quay lại màn hình này."
                else -> "Đưa thẻ hoặc vòng đeo tay NFC sát mặt sau điện thoại."
            },
            modifier = Modifier.padding(top = 12.dp),
            color = if (adapter == null || !nfcEnabled) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Box(
            Modifier.padding(top = 40.dp).background(role.accent().copy(alpha = .10f), CircleShape).padding(56.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isSaving) CircularProgressIndicator()
            else Text("NFC", color = role.accent(), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        }
        Text(
            if (isSaving) "Đã nhận thẻ, đang lưu giao dịch..." else "Giữ thẻ ổn định trong giây lát",
            modifier = Modifier.padding(top = 24.dp),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        if (adapter != null && !nfcEnabled) {
            TextButton(onClick = { context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS)) }) {
                Text("Mở cài đặt NFC")
            }
        }
        if (BuildConfig.DEBUG && role == PosRole.CONVENIENCE) {
            PosPanel {
                Text("Giả lập UID · Chỉ bản debug", fontWeight = FontWeight.Bold, color = PosInk)
                Text(
                    "Dùng trên máy ảo không có NFC. Giá trị được lưu như một lượt đọc NFC để kiểm tra giao diện.",
                    color = PosMuted,
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = debugUid,
                    onValueChange = { debugUid = it; error = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("UID vòng NFC") },
                    placeholder = { Text("Ví dụ: DEMO-NFC-002") },
                    enabled = !isSaving,
                    singleLine = true
                )
                Button(
                    onClick = { saveUid(debugUid) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    enabled = debugUid.isNotBlank() && !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = role.accent())
                ) { Text("Tiếp tục với UID giả lập") }
            }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp)) }
        Spacer(Modifier.height(24.dp))
        Text(
            "Đọc vòng chỉ lưu mã trên thiết bị. Chưa xác nhận danh tính, quyền tham gia hoặc chi phí của hành khách.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
