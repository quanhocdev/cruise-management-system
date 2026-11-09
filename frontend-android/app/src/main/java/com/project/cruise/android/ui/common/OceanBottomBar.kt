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
import com.project.cruise.android.ui.theme.*

@Composable
fun OceanBottomBar(
    isLoggedIn: Boolean,
    currentRoute: String,
    onHomeClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUserClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
    onMessagesClick: () -> Unit,
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
                    "Khám phá",
                    PassengerGlyph.EXPLORE,
                    currentRoute == "passenger_tours",
                    Modifier.weight(1f),
                    onHomeClick,
                )
                NavItem(
                    "Chuyến đi",
                    PassengerGlyph.TICKET,
                    currentRoute == "passenger_bookings",
                    Modifier.weight(1f),
                    if (isLoggedIn) onMyBookingsClick else onLoginClick,
                )
                NavItem(
                    "Tin nhắn",
                    PassengerGlyph.MESSAGE,
                    currentRoute == "passenger_messages",
                    Modifier.weight(1f),
                    if (isLoggedIn) onMessagesClick else onLoginClick,
                )
                NavItem(
                    if (isLoggedIn) "Tài khoản" else "Đăng nhập",
                    PassengerGlyph.PERSON,
                    currentRoute == "passenger_profile",
                    Modifier.weight(1f),
                    if (isLoggedIn) onUserClick else onLoginClick,
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
    val background by
    animateColorAsState(
        if (isSelected) OceanMint.copy(alpha = .20f) else Color.Transparent,
        label = "navBackground",
    )
    val tint by
    animateColorAsState(
        if (isSelected) OceanMint else Color.White.copy(alpha = .75f),
        label = "navTint",
    )
    val iconScale by
    animateFloatAsState(
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