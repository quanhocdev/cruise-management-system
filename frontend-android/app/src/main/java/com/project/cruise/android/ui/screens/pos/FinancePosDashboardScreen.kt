package com.project.cruise.android.ui.screens.pos

import androidx.compose.runtime.Composable

@Composable
fun FinancePosDashboardScreen(
    username: String,
    onLogoutClick: () -> Unit,
    onQrClick: () -> Unit,
    onNfcClick: () -> Unit,
    onManualClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSpendingClick: () -> Unit = {},
    onCorrectionClick: () -> Unit = {},
    onRefundClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {},
    onReportClick: () -> Unit = {}
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
            ),
            PosDashboardAction("Hạn mức chi tiêu", "Tra cứu booking và quản lý hạn mức", "123", onSpendingClick),
            PosDashboardAction("Điều chỉnh chi tiêu", "Hủy hoặc hoàn khoản chi chưa quyết toán", "123", onCorrectionClick),
            PosDashboardAction("Hoàn tiền thanh toán", "Tra cứu và hoàn tiền vé qua VNPay", "123", onRefundClick),
            PosDashboardAction("Quyết toán cuối chuyến", "Chốt bill dịch vụ, thanh toán và biên nhận", "123", onCheckoutClick),
            PosDashboardAction("Thống kê chuyến", "Phát sinh, thu tiền và danh sách bill dịch vụ", "123", onReportClick)
        ),
        notice = "Gửi mã booking để tiếp tục thủ tục tại quầy lễ tân. Gửi mã thành công chưa phải hoàn tất check-in.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}
