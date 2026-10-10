package com.project.cruise.android.ui.screens.pos.onboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.project.cruise.android.ui.screens.pos.PosDashboardAction
import com.project.cruise.android.ui.screens.pos.PosRole
import com.project.cruise.android.ui.screens.pos.RolePosDashboard

@Composable
fun OnboardPosDashboardScreen(
    onLogoutClick: () -> Unit,
    onNfcClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    RolePosDashboard(
        role = PosRole.ONBOARD,
        accent = Color(0xFFDB6B32),
        actions = listOf(
            PosDashboardAction(
                "Quét NFC người tham gia",
                "Xác định hành khách và ghi nhận tham gia hoạt động trên tàu",
                "NFC",
                onNfcClick
            )
        ),
        notice = "Quét NFC để xác định hành khách và ghi nhận tham gia hoạt động trên tàu.",
        onLogoutClick = onLogoutClick,
        onHistoryClick = onHistoryClick
    )
}