package com.project.cruise.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.auth.*

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onHomeClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
    onLogout: () -> Unit,
) {
    val state by authViewModel.meState.collectAsState()
    LaunchedEffect(Unit) { authViewModel.getCurrentUser() }
    ProfileContent(
        state,
        onHomeClick,
        onMyBookingsClick,
        { authViewModel.logout(onLogout) },
        { authViewModel.getCurrentUser() },
    )
}

@Composable
fun ProfileContent(
    state: MeState,
    onHomeClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
    onLogout: () -> Unit,
    onRetry: () -> Unit,
) {
    var confirmLogout by rememberSaveable { mutableStateOf(false) }
    PassengerPage(
        "Tài khoản",
        "Không gian cá nhân của bạn",
        bottomBar = {
            OceanBottomBar(
                true,
                "passenger_profile",
                onHomeClick,
                {},
                {},
                onMyBookingsClick = onMyBookingsClick,
            )
        },
    ) {
        when (state) {
            MeState.Idle,
            MeState.Loading -> item { PassengerLoading() }
            is MeState.Error -> item { PassengerError(state.message, onRetry) }
            is MeState.Success ->
                item {
                    PassengerCard {
                        Surface(color = OceanNavy, shape = RoundedCornerShape(22.dp)) {
                            PassengerIcon(
                                PassengerGlyph.PERSON,
                                OceanMint,
                                Modifier.padding(20.dp).size(38.dp),
                            )
                        }
                        Text(
                            state.username,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = OceanNavy,
                        )
                        PassengerPill(
                            if (state.role.removePrefix("ROLE_") == "PASSENGER") "Hành khách"
                            else state.role.removePrefix("ROLE_")
                        )
                        Text("Chào mừng bạn trở lại OceanCruise.", color = OceanSlate)
                    }
                }
        }
        item { PassengerSection("Hành trình của bạn") }
        item {
            ProfileAction(
                "Chuyến đi của tôi",
                "Xem booking, hành khách và trạng thái đặt chỗ",
                PassengerGlyph.TICKET,
                onMyBookingsClick,
            )
        }
        item {
            ProfileAction(
                "Khám phá tour",
                "Tìm du thuyền và chuyến khởi hành tiếp theo",
                PassengerGlyph.EXPLORE,
                onHomeClick,
            )
        }
        item {
            PassengerCard {
                Text(
                    "OceanCruise",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy,
                )
                Text(
                    "Thông tin chuyến đi và đặt chỗ được đồng bộ từ hệ thống quản lý du thuyền.",
                    color = OceanSlate,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        item {
            OutlinedButton(
                onClick = { confirmLogout = true },
                modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OceanCoral),
                shape = RoundedCornerShape(18.dp),
            ) {
                PassengerIcon(PassengerGlyph.EXIT, OceanCoral)
                Spacer(Modifier.width(10.dp))
                Text("Đăng xuất")
            }
        }
    }
    if (confirmLogout)
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            title = { Text("Đăng xuất tài khoản?") },
            text = { Text("Bạn có thể đăng nhập lại để xem các chuyến đi của mình.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmLogout = false
                        onLogout()
                    }
                ) {
                    Text("Đăng xuất")
                }
            },
            dismissButton = { TextButton(onClick = { confirmLogout = false }) { Text("Ở lại") } },
        )
}

@Composable
private fun ProfileAction(
    title: String,
    subtitle: String,
    glyph: PassengerGlyph,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(22.dp),
    ) {
        Row(
            Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            PassengerIcon(glyph)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy,
                )
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = OceanSlate)
            }
            PassengerIcon(PassengerGlyph.ARROW, modifier = Modifier.size(20.dp))
        }
    }
}
