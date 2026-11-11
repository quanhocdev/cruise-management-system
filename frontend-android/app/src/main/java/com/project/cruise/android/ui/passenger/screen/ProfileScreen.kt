package com.project.cruise.android.ui.passenger.screen

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.project.cruise.android.ui.common.OceanBottomBar
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.passenger.components.profile.*
import com.project.cruise.android.viewmodel.auth.*

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onHomeClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onNotificationClick: () -> Unit = {}, // Thêm callback thông báo
    unreadCount: Long = 0L,              // Thêm số lượng chưa đọc
    onLogout: () -> Unit,
) {
    val state by authViewModel.meState.collectAsState()
    LaunchedEffect(Unit) { authViewModel.getCurrentUser() }
    ProfileContent(
        state = state,
        onHomeClick = onHomeClick,
        onMyBookingsClick = onMyBookingsClick,
        onMessagesClick = onMessagesClick,
        onNotificationClick = onNotificationClick,
        unreadCount = unreadCount,
        onLogout = { authViewModel.logout(onLogout) },
        onRetry = { authViewModel.getCurrentUser() },
    )
}

@Composable
fun ProfileContent(
    state: MeState,
    onHomeClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onNotificationClick: () -> Unit,
    unreadCount: Long,
    onLogout: () -> Unit,
    onRetry: () -> Unit,
) {
    var confirmLogout by rememberSaveable { mutableStateOf(false) }

    PassengerPage(
        "Tài khoản",
        "Không gian cá nhân của bạn",
        bottomBar = {
            OceanBottomBar(
                isLoggedIn = true,
                currentRoute = "passenger_profile",
                unreadCount = unreadCount,
                onHomeClick = onHomeClick,
                onLoginClick = {},
                onUserClick = {}, // Đang ở trang tài khoản rồi nên để trống
                onMyBookingsClick = onMyBookingsClick,
                onMessagesClick = onMessagesClick,
                onNotificationClick = onNotificationClick,
            )
        },
    ) {
        when (state) {
            MeState.Idle,
            MeState.Loading -> item { PassengerLoading() }
            is MeState.Error -> item { PassengerError(state.message, onRetry) }
            is MeState.Success ->
                item { ProfileHeroCard(username = state.username, role = state.role) }
        }

        item { PassengerSection("Hành trình của bạn") }
        item {
            ProfileActionItem(
                title = "Chuyến đi của tôi",
                subtitle = "Xem booking, hành khách và trạng thái đặt chỗ",
                glyph = PassengerGlyph.TICKET,
                onClick = onMyBookingsClick,
            )
        }
        item {
            ProfileActionItem(
                title = "Khám phá tour",
                subtitle = "Tìm du thuyền và chuyến khởi hành tiếp theo",
                glyph = PassengerGlyph.EXPLORE,
                onClick = onHomeClick,
            )
        }
        item { ProfileAboutCard() }
        item { LogoutButton(onClick = { confirmLogout = true }) }
    }

    if (confirmLogout)
        LogoutConfirmDialog(
            onConfirm = {
                confirmLogout = false
                onLogout()
            },
            onDismiss = { confirmLogout = false },
        )
}