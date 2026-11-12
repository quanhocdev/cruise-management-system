package com.project.cruise.android.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import com.project.cruise.android.ui.theme.*

@Composable
fun OceanBottomBar(
    isLoggedIn: Boolean,
    currentRoute: String,
    unreadCount: Long = 0L,
    onHomeClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUserClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onVesselTrackingClick: () -> Unit,
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
                    glyph = PassengerGlyph.EXPLORE,
                    isSelected = currentRoute == "passenger_tours",
                    modifier = Modifier.weight(1f),
                    onClick = onHomeClick,
                )
                NavItem(
                    label = "Chuyến đi",
                    glyph = PassengerGlyph.TICKET,
                    isSelected = currentRoute == "passenger_bookings",
                    modifier = Modifier.weight(1f),
                    onClick = if (isLoggedIn) onMyBookingsClick else onLoginClick,
                )

                // --- ĐẶT ICON GIÁM SÁT NGAY CHÍNH GIỮA (VỊ TRÍ THỨ 3) ---
                NavItem(
                    label = "Giám sát",
                    glyph = PassengerGlyph.COMPASS, // Hoặc thay bằng tên hằng số đúng có trong enum PassengerGlyph của bạn
                    isSelected = currentRoute == "vessel_tracking",
                    modifier = Modifier.weight(1f),
                    onClick = if (isLoggedIn) onVesselTrackingClick else onLoginClick,
                )

                NavItem(
                    label = "Tin nhắn",
                    glyph = PassengerGlyph.MESSAGE,
                    isSelected = currentRoute == "passenger_messages",
                    modifier = Modifier.weight(1f),
                    onClick = if (isLoggedIn) onMessagesClick else onLoginClick,
                )
                NavNotificationItem(
                    label = "Thông báo",
                    unreadCount = unreadCount,
                    isSelected = currentRoute == "passenger_notifications",
                    modifier = Modifier.weight(1f),
                    onClick = if (isLoggedIn) onNotificationClick else onLoginClick,
                )
                NavItem(
                    label = if (isLoggedIn) "Tài khoản" else "Đăng nhập",
                    glyph = PassengerGlyph.PERSON,
                    isSelected = currentRoute == "passenger_profile",
                    modifier = Modifier.weight(1f),
                    onClick = if (isLoggedIn) onUserClick else onLoginClick,
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    glyph: PassengerGlyph,
    isSelected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val background by animateColorAsState(
        if (isSelected) OceanMint.copy(alpha = .20f) else Color.Transparent,
        label = "navBackground",
    )
    val tint by animateColorAsState(
        if (isSelected) OceanMint else Color.White.copy(alpha = .75f),
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
        ) {
            PassengerIcon(glyph, tint)
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = tint,
            maxLines = 1,
        )
    }
}

@Composable
private fun NavNotificationItem(
    label: String,
    unreadCount: Long,
    isSelected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val background by animateColorAsState(
        if (isSelected) OceanMint.copy(alpha = .20f) else Color.Transparent,
        label = "notifNavBackground",
    )
    val tint by animateColorAsState(
        if (isSelected) OceanMint else Color.White.copy(alpha = .75f),
        label = "notifNavTint",
    )
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "notifIconScale",
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
            modifier = Modifier.graphicsLayer {
                scaleX = iconScale
                scaleY = iconScale
            },
            contentAlignment = Alignment.TopEnd
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = label,
                tint = tint, // <--- Đã xóa chữ "kotlin" thừa ở đây
                modifier = Modifier.size(24.dp)
            )
            if (unreadCount > 0) {
                Badge(
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    modifier = Modifier.offset(x = 8.dp, y = (-4).dp)
                ) {
                    Text(
                        text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = tint,
            maxLines = 1,
        )
    }
}