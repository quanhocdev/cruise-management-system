package com.project.cruise.android.ui.screens.pos.shore

import android.app.Activity
import android.media.AudioManager
import android.media.ToneGenerator
import android.nfc.NfcAdapter
import android.nfc.Tag
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.cruise.android.data.dto.nfc.NfcResolveResponse
import com.project.cruise.android.viewmodel.shore.ShoreNfcScanViewModel

@Composable
fun ShoreNfcScanScreen(
    onBackClick: () -> Unit,
    onResolved: (NfcResolveResponse, String) -> Unit,
    viewModel: ShoreNfcScanViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val uiState by viewModel.uiState.collectAsState()

    val nfcAdapter = NfcAdapter.getDefaultAdapter(context)

    DisposableEffect(nfcAdapter, activity) {
        if (nfcAdapter != null && activity != null) {

            val readerCallback = object : NfcAdapter.ReaderCallback {
                override fun onTagDiscovered(tag: Tag?) {
                    if (tag == null) {
                        return
                    }

                    val uid = tag.id.joinToString("") { byte ->
                        "%02X".format(byte.toInt() and 0xFF)
                    }

                    ToneGenerator(
                        AudioManager.STREAM_NOTIFICATION,
                        100
                    ).apply {
                        startTone(
                            ToneGenerator.TONE_PROP_BEEP,
                            150
                        )
                        release()
                    }

                    viewModel.resolveNfc(uid)
                }
            }

            nfcAdapter.enableReaderMode(
                activity,
                readerCallback,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V or
                        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                null
            )

            onDispose {
                nfcAdapter.disableReaderMode(activity)
            }

        } else {
            onDispose { }
        }
    }

    LaunchedEffect(
        uiState.resolved,
        uiState.nfcCardUid
    ) {
        val resolved = uiState.resolved
        val uid = uiState.nfcCardUid

        if (resolved != null && uid != null) {
            onResolved(resolved, uid)
            viewModel.reset()
        }
    }

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

            uiState.isResolving -> {
                CircularProgressIndicator()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Đang xác thực thẻ NFC...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            uiState.error != null -> {
                Text(
                    text = uiState.error ?: "Có lỗi xảy ra.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.clearError()
                    }
                ) {
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

        Button(
            onClick = onBackClick
        ) {
            Text("Quay lại")
        }
    }
}