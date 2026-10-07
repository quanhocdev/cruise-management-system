package com.project.cruise.android.ui.screens.pos

import androidx.compose.runtime.Composable

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
                "Quét QR tham quan",
                "Kiểm tra khách trước khi rời tàu hoặc khi quay lại",
                "QR",
                onQrClick
            ),
            PosDashboardAction(
                "Nhập mã vé",
                "Đối chiếu mã khi không đọc được QR",
                "123",
                onManualClick
            )
        ),
        notice = "Chọn đúng chuyến tham quan để đối chiếu. Mã đã lưu chưa xác nhận khách rời tàu hoặc trở về.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}
