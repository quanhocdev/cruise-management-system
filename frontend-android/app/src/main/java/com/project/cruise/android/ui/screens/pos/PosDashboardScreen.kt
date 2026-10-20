package com.project.cruise.android.ui.screens.pos

import androidx.compose.runtime.Composable
import com.project.cruise.android.ui.screens.pos.convenience.ConveniencePosDashboardScreen
import com.project.cruise.android.ui.screens.pos.onboard.OnboardPosDashboardScreen
import com.project.cruise.android.ui.screens.pos.shore.ShorePosDashboardScreen

@Composable
fun PosDashboardScreen(
    role: PosRole,
    username: String,
    onLogoutClick: () -> Unit,
    onQrClick: () -> Unit,
    onNfcClick: () -> Unit,
    onManualClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSpendingClick: () -> Unit = {},
    onCorrectionClick: () -> Unit = {},
    onRefundClick: () -> Unit = {},
    onRegistrationsClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {}
) {
    when (role) {
        PosRole.FINANCE -> FinancePosDashboardScreen(
            username, onLogoutClick, onQrClick, onNfcClick, onManualClick, onHistoryClick, onSpendingClick, onCorrectionClick, onRefundClick, onCheckoutClick
        )
        PosRole.CONVENIENCE -> ConveniencePosDashboardScreen(
            username, onLogoutClick, onNfcClick, onHistoryClick, onCorrectionClick
        )
        PosRole.ONBOARD -> OnboardPosDashboardScreen(
            username, onLogoutClick, onQrClick, onManualClick, onHistoryClick, onNfcClick, onRegistrationsClick
        )
        PosRole.SHORE -> ShorePosDashboardScreen(
            username, onLogoutClick, onQrClick, onManualClick, onHistoryClick, onNfcClick, onRegistrationsClick
        )
    }
}
