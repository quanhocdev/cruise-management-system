package com.project.cruise.android.ui.screens.pos.onboard

import com.project.cruise.android.ui.screens.pos.*

import androidx.compose.runtime.Composable

@Composable
fun OnboardPosDashboardScreen(
    username: String,
    onLogoutClick: () -> Unit,
    onQrClick: () -> Unit,
    onManualClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onNfcClick: () -> Unit = {},
    onRegistrationsClick: () -> Unit = {}
) {
    RolePosDashboard(
        username = username,
        role = PosRole.ONBOARD,
        accent = PosRole.ONBOARD.accent(),
        actions = listOf(
            PosDashboardAction("Khách đăng ký hoạt động", "Tra cứu và xác nhận khách tham gia", "DS", onRegistrationsClick),
            PosDashboardAction("Quét NFC dịch vụ", "Nhận diện khách và ghi nhận lượt sử dụng", "NFC", onNfcClick),
            PosDashboardAction(
                "Quét QR vé khách",
                "Đọc mã vé để kiểm tra hành khách và chuyến tàu",
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
        notice = "Lượt quét được lưu để kiểm tra. Chỉ cho khách lên tàu sau khi vé và chuyến đã được xác minh.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}
