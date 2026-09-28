package com.project.cruise.android.ui.screens.pos

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun OnboardPosDashboardScreen(
    username: String,
    onLogoutClick: () -> Unit,
    onQrClick: () -> Unit,
    onManualClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    RolePosDashboard(
        username = username,
        role = PosRole.ONBOARD,
        accent = PosRole.ONBOARD.accent(),
        actions = listOf(
            PosDashboardAction(
                "Quét QR vé khách",
                "Đọc mã vé để kiểm tra hành khách và chuyến tàu",
                "QR",
                onQrClick
            ),
            PosDashboardAction(
                "Nhập mã vé",
                "Dùng khi máy ảo hoặc camera không đọc được QR",
                "123",
                onManualClick
            )
        ),
        notice = "Quét vé chỉ bắt đầu bước kiểm tra. Chỉ xác nhận khách lên tàu sau khi backend trả đúng hành khách, chuyến và trạng thái vé hợp lệ.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}
