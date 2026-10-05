package com.project.cruise.android.ui.screens.pos

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ShorePosDashboardScreen(
    onLogoutClick: () -> Unit,
    onNfcClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    RolePosDashboard(
        role = PosRole.SHORE,
        accent = Color(0xFF18794E),
        actions = listOf(
            PosDashboardAction(
                "Quét NFC người tham gia",
                "Xác định hành khách và ghi nhận tham gia hoạt động tham quan bờ",
                "NFC",
                onNfcClick
            )
        ),
        notice = "Quét NFC để xác định hành khách và ghi nhận tham gia hoạt động tham quan bờ.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}