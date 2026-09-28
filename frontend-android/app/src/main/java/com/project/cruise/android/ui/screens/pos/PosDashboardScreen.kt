package com.project.cruise.android.ui.screens.pos

import androidx.compose.runtime.Composable

@Composable
fun PosDashboardScreen(
    role: PosRole,
    username: String,
    onLogoutClick: () -> Unit,
    onQrClick: () -> Unit,
    onNfcClick: () -> Unit,
    onManualClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    when (role) {
        PosRole.FINANCE -> FinancePosDashboardScreen(
            username, onLogoutClick, onQrClick, onNfcClick, onManualClick, onHistoryClick
        )
        PosRole.CONVENIENCE -> ConveniencePosDashboardScreen(
            username, onLogoutClick, onNfcClick, onHistoryClick
        )
        PosRole.ONBOARD -> OnboardPosDashboardScreen(
            username, onLogoutClick, onQrClick, onManualClick, onHistoryClick
        )
        PosRole.SHORE -> ShorePosDashboardScreen(
            username, onLogoutClick, onNfcClick, onHistoryClick
        )
    }
}
