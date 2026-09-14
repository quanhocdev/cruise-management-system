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
                "Xác định người sử dụng sản phẩm hoặc dịch vụ tiện ích",
                "NFC",
                onNfcClick
            )
        ),
        notice = "Role CONVENIENCE chỉ dùng NFC. Ghi nhận dịch vụ sẽ được bật khi backend cung cấp API.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}
