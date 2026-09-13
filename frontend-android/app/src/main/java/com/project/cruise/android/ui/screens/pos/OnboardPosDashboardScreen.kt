package com.project.cruise.android.ui.screens.pos

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun OnboardPosDashboardScreen(
    onLogoutClick: () -> Unit,
    onNfcClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    RolePosDashboard(
        role = PosRole.ONBOARD,
        accent = Color(0xFFDB6B32),
        actions = listOf(
            PosDashboardAction(
                "Quét NFC người tham gia",
                "Xác định hành khách tham gia hoạt động trên tàu",
                "NFC",
                onNfcClick
            )
        ),
        notice = "Role ONBOARD không hiển thị QR. Ghi nhận hoạt động sẽ được bật khi backend cung cấp API.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}
