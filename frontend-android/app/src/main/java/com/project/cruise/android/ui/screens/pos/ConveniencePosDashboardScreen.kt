package com.project.cruise.android.ui.screens.pos

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ConveniencePosDashboardScreen(
    username: String,
    onLogoutClick: () -> Unit,
    onNfcClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    RolePosDashboard(
        username = username,
        role = PosRole.CONVENIENCE,
        accent = PosRole.CONVENIENCE.accent(),
        actions = listOf(
            PosDashboardAction(
                "Quét NFC hành khách",
                "Xác định người sử dụng sản phẩm hoặc dịch vụ tiện ích",
                "NFC",
                onNfcClick
            )
        ),
        notice = "Đọc vòng NFC để lưu mã nhận diện. Việc đọc vòng chưa xác nhận sử dụng dịch vụ hoặc phát sinh chi phí.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}
