package com.project.cruise.android.ui.screens.pos

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ShorePosDashboardScreen(
    username: String,
    onLogoutClick: () -> Unit,
    onQrClick: () -> Unit,
    onManualClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    RolePosDashboard(
        username = username,
        role = PosRole.SHORE,
        accent = PosRole.SHORE.accent(),
        actions = listOf(
            PosDashboardAction(
                "Quét QR khách tham quan",
                "Kiểm tra khách trước khi rời tàu hoặc khi quay lại",
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
        notice = "Chọn đúng chuyến tham quan rồi kiểm tra khách. Lượt quét chỉ là bản ghi cục bộ cho tới khi backend xác nhận rời tàu hoặc quay lại.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}
