package com.project.cruise.android.ui.screens.pos

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ConveniencePosDashboardScreen(
    onLogoutClick: () -> Unit,
    onNfcClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    RolePosDashboard(
        role = PosRole.CONVENIENCE,
        accent = Color(0xFF7C3AED),
        actions = listOf(
            PosDashboardAction(
                "Quét NFC hành khách",
                "Xác định hành khách và ghi nhận sản phẩm hoặc dịch vụ tiện ích",
                "NFC",
                onNfcClick
            )
        ),
        notice = "Nhân viên quét NFC của hành khách, sau đó chọn sản phẩm hoặc dịch vụ để ghi nhận.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}