package com.project.cruise.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
fun OceanBottomBar(
    isLoggedIn: Boolean,
    currentRoute: String,
    onHomeClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUserClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
) {
    OceanTheme {
        Surface(
            Modifier.fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = OceanNavy,
            shape = RoundedCornerShape(26.dp),
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
    Column(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) OceanMint.copy(alpha = .18f) else Color.Transparent)
            .semantics { selected = isSelected }
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        PassengerIcon(glyph, if (isSelected) OceanMint else Color.White.copy(alpha = .8f))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) OceanMint else Color.White,
        )
    }
}
