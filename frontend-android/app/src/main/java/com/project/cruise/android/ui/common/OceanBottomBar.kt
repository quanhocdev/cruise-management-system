package com.project.cruise.android.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

/** Các route con của Tài khoản: khi đang ở đó, tab "Tài khoản" vẫn sáng. */
private val PROFILE_ROUTES = setOf("passenger_profile", "passenger_bookings", "passenger_messages")

/**
 * Thanh dưới còn 3 mục: Khám phá, Thông báo, Tài khoản.
 * Tin nhắn và Chuyến đi của tôi nằm trong Tài khoản; Giám sát nằm trong chi tiết booking.
 * Ba tham số cuối chỉ giữ để các nơi gọi cũ không vỡ build, có thể xóa dần.
 */
@Composable
fun OceanBottomBar(
    isLoggedIn: Boolean,
    currentRoute: String,
    unreadCount: Long = 0L,
    onHomeClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUserClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onMyBookingsClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    onVesselTrackingClick: () -> Unit = {},
) {
    OceanTheme {
        Surface(
            Modifier.fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = OceanNavy,
            shape = RoundedCornerShape(28.dp),
            shadowElevation = 12.dp,
        ) {
            Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                NavItem(
                    label = "Khám phá",
                    isSelected = currentRoute == "passenger_tours",
                    modifier = Modifier.weight(1f),
                    onClick = onHomeClick,
                ) { tint -> PassengerIcon(PassengerGlyph.EXPLORE, tint) }

                NavItem(
                    label = "Thông báo",
                    isSelected = currentRoute == "passenger_notifications",
                    modifier = Modifier.weight(1f),
                    onClick = if (isLoggedIn) onNotificationClick else onLoginClick,
                ) { tint ->
                    Box(contentAlignment = Alignment.TopEnd) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Thông báo",
                            tint = tint,
                            modifier = Modifier.size(24.dp),
                        )
                        if (unreadCount > 0) {
                            Badge(
                                containerColor = OceanGold,
                                contentColor = OceanNavy,
                                modifier = Modifier.offset(x = 8.dp, y = (-4).dp),
                            ) {
                                Text(
                                    text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

                NavItem(
                    label = if (isLoggedIn) "Tài khoản" else "Đăng nhập",
                    isSelected = currentRoute in PROFILE_ROUTES,
                    modifier = Modifier.weight(1f),
                    onClick = if (isLoggedIn) onUserClick else onLoginClick,
                ) { tint -> PassengerIcon(PassengerGlyph.PERSON, tint) }
            }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    isSelected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
    icon: @Composable (tint: Color) -> Unit,
) {
    val background by animateColorAsState(
        if (isSelected) OceanGold.copy(alpha = .20f) else Color.Transparent,
        label = "navBackground",
    )
    val tint by animateColorAsState(
        if (isSelected) OceanGold else Color.White.copy(alpha = .75f),
        label = "navTint",
    )
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "navIconScale",
    )

    Column(
        modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .semantics { selected = isSelected }
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            Modifier.graphicsLayer {
                scaleX = iconScale
                scaleY = iconScale
            }
        ) { icon(tint) }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = tint,
            maxLines = 1,
        )
    }
}