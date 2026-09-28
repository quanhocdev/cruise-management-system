package com.project.cruise.android.ui.screens.pos

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun OnboardPosDashboardScreen(
    username: String,
    onLogoutClick: () -> Unit,
    onNfcClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    RolePosDashboard(
        username = username,
        role = PosRole.ONBOARD,
        accent = PosRole.ONBOARD.accent(),
        actions = listOf(
            PosDashboardAction(
                "Quét NFC người tham gia",
                "Xác định hành khách tham gia hoạt động trên tàu",
                "NFC",
                onNfcClick
            )
        ),
        notice = "Đọc vòng NFC của người tham gia. Bản ghi đọc vòng chưa phải xác nhận tham gia hoạt động.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}
