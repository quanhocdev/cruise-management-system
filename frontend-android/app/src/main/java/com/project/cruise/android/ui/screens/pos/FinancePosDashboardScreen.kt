package com.project.cruise.android.ui.screens.pos

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun FinancePosDashboardScreen(
    username: String,
    onLogoutClick: () -> Unit,
    onQrClick: () -> Unit,
    onNfcClick: () -> Unit,
    onManualClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    RolePosDashboard(
        username = username,
        role = PosRole.FINANCE,
        accent = PosRole.FINANCE.accent(),
        actions = listOf(
            PosDashboardAction(
                "Quét QR trong email",
                "Nhận diện hành khách để làm thủ tục check-in",
                "QR",
                onQrClick
            ),
            PosDashboardAction(
                "Quét thẻ NFC",
                "Dùng NFC khi làm thủ tục hoặc kiểm tra hành khách",
                "NFC",
                onNfcClick
            ),
            PosDashboardAction(
                "Nhập mã thủ công",
                "Nhập mã trên email khi camera không đọc được QR",
                "123",
                onManualClick
            ),
            PosDashboardAction(
                "Hoàn tất checkout",
                "Sẽ hỗ trợ QR, NFC và mã nhập tay sau khi backend có API checkout",
                "OUT",
                onClick = {},
                enabled = false,
                status = "Đang chờ API backend"
            )
        ),
        notice = "Gửi mã booking để tiếp tục thủ tục tại quầy lễ tân. Gửi mã thành công chưa phải hoàn tất check-in.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}
