package com.project.cruise.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.components.OceanBottomBar
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.MeState

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onHomeClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
    onLogout: () -> Unit,
    onNotificationClick: () -> Unit
) {
    val meState by authViewModel.meState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.getCurrentUser()
    }

    Scaffold(
        bottomBar = {
            OceanBottomBar(
                isLoggedIn = true,
                currentRoute = "passenger_profile",
                onHomeClick = onHomeClick,
                onLoginClick = {},
                onUserClick = {},
                onNotificationClick = onNotificationClick
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            OceanPage {
                Text(
                    "Tài Khoản Của Tôi",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy
                )
                Spacer(Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        when (val state = meState) {
                            is MeState.Success -> {
                                Text("Họ tên: ${state.username}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OceanNavy)
                                Text("Vai trò: ${state.role}", style = MaterialTheme.typography.bodyMedium, color = OceanTeal)
                            }
                            else -> CircularProgressIndicator(color = OceanTeal)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(8.dp)) {
                        TextButton(onClick = onMyBookingsClick, modifier = Modifier.fillMaxWidth()) {
                            Text("Booking của tôi", color = OceanNavy, fontWeight = FontWeight.SemiBold)
                        }
                        Divider(color = OceanPearl)
                        TextButton(onClick = { authViewModel.logout(onLogout) }, modifier = Modifier.fillMaxWidth()) {
                            Text("Đăng xuất", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}