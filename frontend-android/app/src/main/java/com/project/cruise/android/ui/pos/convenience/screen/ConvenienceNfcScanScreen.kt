
package com.project.cruise.android.ui.pos.convenience.screen

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.cruise.android.data.dto.nfc.NfcResolveResponse
import com.project.cruise.android.ui.components.nfc.NfcReaderEffect
import com.project.cruise.android.ui.components.nfc.rememberNfcAdapter
import com.project.cruise.android.ui.pos.convenience.components.ConvenienceBackground
import com.project.cruise.android.ui.pos.convenience.components.nfcscan.*
import com.project.cruise.android.viewmodel.convenience.ConvenienceNfcScanViewModel

@Composable
fun ConvenienceNfcScanScreen(
    onBackClick: () -> Unit,
    onResolved: (NfcResolveResponse, String) -> Unit,
    viewModel: ConvenienceNfcScanViewModel = viewModel()
) {
    val context = LocalContext.current
    val adapter = rememberNfcAdapter()
    val uiState by viewModel.uiState.collectAsState()

    var manualNfcUid by remember { mutableStateOf("") }

    // Quét NFC thật: giữ nguyên luồng hiện tại.
    NfcReaderEffect(
        adapter = adapter,
        enabled = !uiState.isResolving,
        onUidRead = { uid ->
            viewModel.resolveNfc(uid)
        }
    )

    // Khi xác thực thành công, chuyển sang màn hình sử dụng tiện ích.
    LaunchedEffect(uiState.resolved, uiState.nfcCardUid) {
        val response = uiState.resolved
        val nfcCardUid = uiState.nfcCardUid

        if (response != null && !nfcCardUid.isNullOrBlank()) {
            onResolved(response, nfcCardUid)
        }
    }

    val status = when {
        adapter == null -> NfcScanStatus.UNSUPPORTED
        !adapter.isEnabled -> NfcScanStatus.DISABLED
        uiState.isResolving -> NfcScanStatus.RESOLVING
        else -> NfcScanStatus.READY
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(ConvenienceBackground, Color.White)
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NfcScanBackButton(
            onClick = onBackClick,
            enabled = !uiState.isResolving,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(32.dp))

        // Khu vực quét NFC thật.
        NfcScanHeading(status)

        NfcScanIndicator(
            isResolving = uiState.isResolving,
            modifier = Modifier.padding(top = 24.dp)
        )

        NfcScanCaption(
            uiState.isResolving,
            Modifier.padding(top = 8.dp)
        )

        if (status == NfcScanStatus.DISABLED) {
            NfcSettingsButton(
                onClick = {
                    context.startActivity(
                        Intent(Settings.ACTION_NFC_SETTINGS)
                    )
                }
            )
        }

        uiState.error?.let {
            NfcScanError(
                it,
                Modifier.padding(top = 12.dp)
            )
        }

        Spacer(Modifier.height(28.dp))

        // Khu vực nhập UID thủ công để kiểm thử không cần NFC.
        Text(
            text = "Hoặc nhập mã NFC để kiểm thử",
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = manualNfcUid,
            onValueChange = { manualNfcUid = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Mã NFC UID") },
            placeholder = { Text("Ví dụ: 04AABBCC") },
            singleLine = true,
            enabled = !uiState.isResolving
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val uid = manualNfcUid.trim()
                if (uid.isNotEmpty()) {
                    viewModel.resolveNfc(uid)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = manualNfcUid.isNotBlank() &&
                    !uiState.isResolving
        ) {
            if (uiState.isResolving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text("Đang xác thực...")
            } else {
                Text("Gửi UID thử nghiệm")
            }
        }

        Spacer(Modifier.height(24.dp))

        NfcScanFooterNote()
    }
}
