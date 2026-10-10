package com.project.cruise.android.ui.screens.pos.shore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.cruise.android.data.dto.nfc.NfcResolveResponse
import com.project.cruise.android.ui.components.nfc.SimpleNfcScanContent
import com.project.cruise.android.viewmodel.shore.ShoreNfcScanViewModel

@Composable
fun ShoreNfcScanScreen(
    onBackClick: () -> Unit,
    onResolved: (NfcResolveResponse, String) -> Unit,
    viewModel: ShoreNfcScanViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.resolved, uiState.nfcCardUid) {
        val resolved = uiState.resolved
        val uid = uiState.nfcCardUid

        if (resolved != null && uid != null) {
            onResolved(resolved, uid)
            viewModel.reset()
        }
    }

    SimpleNfcScanContent(
        isResolving = uiState.isResolving,
        error = uiState.error,
        onUidRead = { uid -> viewModel.resolveNfc(uid) },
        onClearError = { viewModel.clearError() },
        onBackClick = onBackClick
    )
}