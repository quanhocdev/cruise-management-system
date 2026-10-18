package com.project.cruise.android.ui.screens.pos.convenience

import com.project.cruise.android.ui.screens.pos.*

import androidx.compose.runtime.Composable

@Composable
fun ConveniencePosDashboardScreen(
    username: String,
    onLogoutClick: () -> Unit,
    onNfcClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onCorrectionClick: () -> Unit = {}
) {
    RolePosDashboard(
        username = username,
        role = PosRole.CONVENIENCE,
        accent = PosRole.CONVENIENCE.accent(),
        actions = listOf(
            PosDashboardAction(
                "Chạm vòng tay NFC",
                "Đọc mã vòng và xem danh mục tiện ích",
                "NFC",
                onNfcClick
            ),
            PosDashboardAction("Điều chỉnh chi tiêu", "Hủy khoản bạn ghi trong 10 phút", "123", onCorrectionClick)
        ),
        notice = "Đọc vòng NFC để lưu mã nhận diện. Việc đọc vòng chưa xác nhận sử dụng dịch vụ hoặc phát sinh chi phí.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}
