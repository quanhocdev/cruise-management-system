package com.project.cruise.android.ui.screens.pos

import androidx.compose.runtime.Composable
import com.project.cruise.android.ui.screens.pos.convenience.ConveniencePosDashboardScreen
import com.project.cruise.android.ui.screens.pos.onboard.OnboardPosDashboardScreen
import com.project.cruise.android.ui.screens.pos.shore.ShorePosDashboardScreen

@Composable
fun PosDashboardScreen(
    role: PosRole,
    onLogoutClick: () -> Unit,
    onQrClick: () -> Unit,
    onNfcClick: () -> Unit,
    onManualClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    when (role) {
        PosRole.FINANCE -> FinancePosDashboardScreen(
            onLogoutClick, onQrClick, onNfcClick, onManualClick, onHistoryClick
        )
        PosRole.CONVENIENCE -> ConveniencePosDashboardScreen(
            onLogoutClick, onNfcClick, onHistoryClick
        )
        PosRole.ONBOARD -> OnboardPosDashboardScreen(
            onLogoutClick, onNfcClick, onHistoryClick
        )
        PosRole.SHORE -> ShorePosDashboardScreen(
            onLogoutClick, onNfcClick, onHistoryClick
        )
    }
}
