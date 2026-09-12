package com.project.cruise.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
fun OceanBottomBar(
    isLoggedIn: Boolean,
    currentRoute: String,
    onHomeClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUserClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    val isHomeSelected = currentRoute == "passenger_tours"
    val isProfileSelected = currentRoute == "passenger_profile"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        color = OceanNavy,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. ICON NGƯỜI (Profile / Login)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isProfileSelected) OceanMint.copy(alpha = 0.25f) else Color.Transparent)
                    .clickable {
                        if (isLoggedIn) onUserClick() else onLoginClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Tài khoản",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 2. ICON NHÀ (Trang chủ Tour Public)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(if (isHomeSelected) OceanMint.copy(alpha = 0.25f) else Color.Transparent)
                    .clickable(onClick = onHomeClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Trang chủ",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            // 3. ICON CHUÔNG (Thông báo)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onNotificationClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Thông báo",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}