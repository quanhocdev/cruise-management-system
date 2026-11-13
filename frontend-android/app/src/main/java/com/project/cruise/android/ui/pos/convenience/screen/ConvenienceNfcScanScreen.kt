package com.project.cruise.android.ui.pos.convenience.screen

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    // Đọc thẻ, phát bíp, bỏ qua khi đang xác thực; gửi UID lên backend để xác định hành khách.
    NfcReaderEffect(
        adapter = adapter,
        enabled = !uiState.isResolving,
        onUidRead = { uid -> viewModel.resolveNfc(uid) }
    )

    LaunchedEffect(uiState.resolved, uiState.nfcCardUid) {
        val response = uiState.resolved
        val nfcCardUid = uiState.nfcCardUid

        if (response != null && !nfcCardUid.isNullOrBlank()) {
            onResolved(response, nfcCardUid)
        }
    }

    val status =
        when {
            adapter == null -> NfcScanStatus.UNSUPPORTED
            !adapter.isEnabled -> NfcScanStatus.DISABLED
            uiState.isResolving -> NfcScanStatus.RESOLVING
            else -> NfcScanStatus.READY
        }

    Column(
        modifier =
            Modifier.fillMaxSize()
                .background(Brush.verticalGradient(listOf(ConvenienceBackground, Color.White)))
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NfcScanBackButton(
            onClick = onBackClick,
            enabled = !uiState.isResolving,
            modifier = Modifier.align(Alignment.Start),
        )

        Spacer(Modifier.weight(1f))

        NfcScanHeading(status)

        NfcScanIndicator(
            isResolving = uiState.isResolving,
            modifier = Modifier.padding(top = 24.dp),
        )

        NfcScanCaption(uiState.isResolving, Modifier.padding(top = 8.dp))

        if (status == NfcScanStatus.DISABLED) {
            NfcSettingsButton(
                onClick = { context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS)) },
            )
        }

        uiState.error?.let { NfcScanError(it, Modifier.padding(top = 12.dp)) }

        Spacer(Modifier.weight(1f))

        NfcScanFooterNote()
    }
}