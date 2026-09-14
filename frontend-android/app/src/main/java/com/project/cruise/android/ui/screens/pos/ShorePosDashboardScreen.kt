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
                "Xác định hành khách tham gia chuyến tham quan bờ",
                "NFC",
                onNfcClick
            )
        ),
        notice = "Role SHORE không hiển thị QR. Ghi nhận chuyến tham quan sẽ được bật khi backend cung cấp API.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}
