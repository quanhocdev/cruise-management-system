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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.cruise.android.data.dto.convenience.NfcResolveResponse
import com.project.cruise.android.viewmodel.ConvenienceNfcScanViewModel

@Composable
fun ConvenienceNfcScanScreen(
    onBackClick: () -> Unit,
    onResolved: (NfcResolveResponse, String) -> Unit,
    viewModel: ConvenienceNfcScanViewModel = viewModel()
) {
    val context = LocalContext.current

    val activity = remember(context) {
        context.findActivity()
    }

    val adapter = remember(context) {
        NfcAdapter.getDefaultAdapter(context)
    }

    val uiState by viewModel.uiState.collectAsState()

    DisposableEffect(activity, adapter) {

        if (activity != null && adapter != null && adapter.isEnabled) {

            adapter.enableReaderMode(
                activity,
                { tag ->

                    if (uiState.isResolving) {
                        return@enableReaderMode
                    }

                    val uid = tag.id.joinToString("") { byte ->
                        "%02X".format(byte.toInt() and 0xFF)
                    }

                    viewModel.resolveNfc(uid)
                },
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V or
                        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                null
            )
        }

        onDispose {
            if (activity != null && adapter != null) {
                adapter.disableReaderMode(activity)
            }
        }
    }

    LaunchedEffect(uiState.resolved, uiState.nfcCardUid) {

        val response = uiState.resolved
        val nfcCardUid = uiState.nfcCardUid

        if (response != null && !nfcCardUid.isNullOrBlank()) {
            onResolved(
                response,
                nfcCardUid
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TextButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.Start),
            enabled = !uiState.isResolving
        ) {
            Text("← Quay lại POS")
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "Quét NFC hành khách",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = when {
                adapter == null ->
                    "Điện thoại này không hỗ trợ NFC."

                !adapter.isEnabled ->
                    "NFC đang tắt. Hãy bật NFC trong Cài đặt rồi quay lại màn hình này."

                uiState.isResolving ->
                    "Đã nhận thẻ. Đang xác định hành khách..."

                else ->
                    "Đưa thẻ NFC của hành khách sát mặt sau điện thoại."
            },
            modifier = Modifier.padding(top = 12.dp),
            color = if (
                adapter == null || !adapter.isEnabled
            ) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .padding(top = 40.dp)
                .background(
                    Color(0xFFEDE9FE),
                    CircleShape
                )
                .padding(56.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isResolving) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = "NFC",
                    color = Color(0xFF7C3AED),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = if (uiState.isResolving) {
                "Đang kiểm tra thẻ..."
            } else {
                "Giữ thẻ ổn định trong giây lát"
            },
            modifier = Modifier.padding(top = 24.dp),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        if (adapter != null && !adapter.isEnabled) {
            TextButton(
                onClick = {
                    context.startActivity(
                        Intent(Settings.ACTION_NFC_SETTINGS)
                    )
                }
            ) {
                Text("Mở cài đặt NFC")
            }
        }

        uiState.error?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 12.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "NFC được dùng để xác định hành khách. Sau đó nhân viên sẽ chọn sản phẩm hoặc dịch vụ tiện ích.",
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