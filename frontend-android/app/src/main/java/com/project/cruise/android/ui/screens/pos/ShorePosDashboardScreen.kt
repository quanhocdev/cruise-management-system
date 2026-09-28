package com.project.cruise.android.ui.screens.pos

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ShorePosDashboardScreen(
    username: String,
    onLogoutClick: () -> Unit,
    onNfcClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    RolePosDashboard(
        username = username,
        role = PosRole.SHORE,
        accent = PosRole.SHORE.accent(),
        actions = listOf(
            PosDashboardAction(
                "Quét NFC người tham gia",
                "Xác định hành khách tham gia chuyến tham quan bờ",
                "NFC",
                onNfcClick
            )
        ),
        notice = "Đọc vòng NFC tại điểm tham quan. Bản ghi đọc vòng chưa xác nhận khách đã khởi hành hoặc trở về.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}
