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

    PosPage {
        PosTopBar("Đọc vòng NFC", role, onBackClick, !isSaving)
        PosPanel {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)) {
                PosBadge(when { adapter == null -> "Thiết bị không hỗ trợ"; !nfcEnabled -> "NFC đang tắt"; else -> "Sẵn sàng đọc vòng" },
                    if(adapter == null || !nfcEnabled) PosAmber else role.accent())
                Text("Chạm vòng tay\nđể bắt đầu", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Box(Modifier.size(224.dp), contentAlignment = Alignment.Center) {
                    androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                        drawCircle(role.accent().copy(alpha=.05f))
                        drawCircle(role.accent().copy(alpha=.10f), radius = size.minDimension*.38f,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()))
                        drawCircle(role.accent().copy(alpha=.12f), radius = size.minDimension*.28f)
                    }
                    if(isSaving) CircularProgressIndicator(color = role.accent())
                    else PosGlyph(PosSymbol.NFC, role.accent(), Modifier.size(72.dp))
                }
                Text(when {
                    isSaving -> "Đã nhận vòng, đang lưu mã…"
                    adapter == null -> "Thiết bị này không có đầu đọc NFC."
                    !nfcEnabled -> "Bật NFC trong cài đặt rồi quay lại."
                    else -> "Giữ vòng tay sát mặt sau điện thoại trong giây lát."
                }, color = PosMuted, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
                if(adapter != null && !nfcEnabled) PosPrimaryButton("Mở cài đặt NFC", role, {
                    context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                })
            }
        }
        if (BuildConfig.DEBUG && role == PosRole.CONVENIENCE) {
            PosPanel {
                PosBadge("CHỈ BẢN DEBUG", role.accent())
                Text("Kiểm thử không cần vòng tay", fontWeight = FontWeight.Bold)
                Text("Nhập UID để thử luồng trên máy ảo.", color = PosMuted, style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(value = debugUid, onValueChange = { debugUid = it; error = null },
                    modifier = Modifier.fillMaxWidth(), label = { Text("UID vòng NFC") },
                    placeholder = { Text("Ví dụ: SEA-NFC-001") }, enabled = !isSaving, singleLine = true,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
                PosPrimaryButton("Đọc UID giả lập", role, { saveUid(debugUid) }, debugUid.isNotBlank(), isSaving)
            }
        }
        error?.let { PosNotice(it, warning = true) }
        PosNotice("Đọc vòng chỉ lưu mã trên thiết bị. Danh tính và quyền sử dụng của khách cần được xác minh.")
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
