package com.project.cruise.android.ui.screens.pos

import androidx.compose.runtime.Composable

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
                "Quét QR booking",
                "Đọc vé điện tử hoặc email của khách",
                "QR",
                onQrClick
            ),
            PosDashboardAction(
                "Đọc vòng NFC",
                "Lưu mã vòng tay của hành khách",
                "NFC",
                onNfcClick
            ),
            PosDashboardAction(
                "Nhập mã booking",
                "Dùng khi không đọc được QR",
                "123",
                onManualClick
            )
        ),
        notice = "Gửi mã booking để tiếp tục thủ tục tại quầy lễ tân. Gửi mã thành công chưa phải hoàn tất check-in.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}
