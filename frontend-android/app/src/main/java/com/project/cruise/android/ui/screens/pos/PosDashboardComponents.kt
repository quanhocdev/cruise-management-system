package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.BuildConfig
import com.project.cruise.android.data.repository.PosTransactionQueue
import com.project.cruise.android.ui.theme.OceanTheme

internal val PosBackground = Color(0xFFF5F8F7)
internal val PosInk = Color(0xFF173B3B)
internal val PosMuted = Color(0xFF526564)
internal fun PosRole.accent() = when (this) {
    PosRole.FINANCE -> Color(0xFF006A69)
    PosRole.CONVENIENCE -> Color(0xFF6E43A5)
    PosRole.ONBOARD -> Color(0xFFA94B20)
    PosRole.SHORE -> Color(0xFF18794E)
}

@Composable
internal fun PosTheme(content: @Composable () -> Unit) {
    OceanTheme {
        MaterialTheme(colorScheme = lightColorScheme(
            primary = Color(0xFF006A69), onPrimary = Color.White,
            background = PosBackground, surface = Color.White,
            onSurface = PosInk, onSurfaceVariant = PosMuted,
            surfaceContainerLow = Color(0xFFEAF2EF)
        ), content = content)
    }
}

@Composable
internal fun PosBadge(text: String, color: Color) {
    Surface(color = color.copy(alpha = .10f), shape = RoundedCornerShape(8.dp)) {
        Text(text, Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = color,
            style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun PosPanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(Modifier.fillMaxWidth(), color = Color.White, shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

internal data class PosDashboardAction(
    val title: String, val description: String, val symbol: String,
    val onClick: () -> Unit, val enabled: Boolean = true, val status: String? = null
)

@Composable
internal fun RolePosDashboard(
    username: String, role: PosRole, accent: Color, actions: List<PosDashboardAction>, notice: String,
    onLogoutClick: () -> Unit, onHistoryClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val queue = remember(context) { PosTransactionQueue(context) }
    val pendingCount by queue.observePendingCount(role.apiRole).collectAsState(initial = 0)
    var account by rememberSaveable { mutableStateOf(false) }
    var confirmLogout by rememberSaveable { mutableStateOf(false) }
    PosTheme {
        Scaffold(containerColor = PosBackground, bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(selected = !account, onClick = { account = false },
                    icon = { Text("⌂") }, label = { Text("Tác vụ") })
                NavigationBarItem(selected = false, onClick = onHistoryClick,
                    icon = { Text("≡") }, label = { Text("Lịch sử") })
                NavigationBarItem(selected = account, onClick = { account = true },
                    icon = { Text("◎") }, label = { Text("Tài khoản") })
            }
        }) { padding ->
            Column(Modifier.padding(padding).fillMaxSize().widthIn(max = 720.dp)
                .verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(color = accent, shape = RoundedCornerShape(14.dp)) {
                        Text("POS", Modifier.padding(14.dp), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("CRUISE POS", color = PosInk, fontWeight = FontWeight.Bold)
                        Text("Không gian làm việc", color = PosMuted, style = MaterialTheme.typography.bodySmall)
                    }
                }
                PosPanel {
                    Text(username, style = MaterialTheme.typography.titleMedium, color = PosInk, fontWeight = FontWeight.Bold)
                    PosBadge(role.apiRole, accent)
                    Text(if (account) "Tài khoản nhân viên" else role.title,
                        style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PosInk)
                    Text(role.subtitle, color = PosMuted)
                    HorizontalDivider(color = PosBackground)
                    Text("Thiết bị • ${BuildConfig.POS_TERMINAL_CODE}", color = PosMuted,
                        style = MaterialTheme.typography.bodySmall)
                }
                if (account) {
                    PosPanel {
                        Text("Phiên làm việc", fontWeight = FontWeight.Bold, color = PosInk)
                        Text("Bạn đang sử dụng POS dành cho ${role.title.lowercase()}. Đăng xuất để sử dụng tài khoản khác.", color = PosMuted)
                        OutlinedButton(onClick = { confirmLogout = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                            Text("Đăng xuất")
                        }
                    }
                } else {
                    if (pendingCount > 0) {
                        Surface(color = Color(0xFFFFEED0), shape = RoundedCornerShape(14.dp)) {
                            Text("$pendingCount bản ghi chưa được xác nhận. Xem trạng thái trong lịch sử.",
                                Modifier.padding(16.dp), color = Color(0xFF745014))
                        }
                    }
                    Text("Sẵn sàng phục vụ", style = MaterialTheme.typography.titleLarge,
                        color = PosInk, fontWeight = FontWeight.Bold)
                    actions.filter { it.enabled }.forEachIndexed { index, action ->
                        Card(onClick = action.onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = if (index == 0) accent else Color.White)) {
                            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text(action.symbol, fontWeight = FontWeight.Bold,
                                    color = if (index == 0) Color.White else accent)
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(action.title, fontWeight = FontWeight.Bold,
                                        color = if (index == 0) Color.White else PosInk)
                                    Text(action.description, style = MaterialTheme.typography.bodySmall,
                                        color = if (index == 0) Color.White.copy(alpha = .9f) else PosMuted)
                                }
                                Text("›", color = if (index == 0) Color.White else accent)
                            }
                        }
                    }
                    PosPanel {
                        Text("Lưu ý khi thao tác", color = PosInk, fontWeight = FontWeight.Bold)
                        Text(notice, color = PosMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                    OutlinedButton(onClick = onHistoryClick, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                        Text("Xem lịch sử thao tác")
                    }
                }
            }
        }
        if (confirmLogout) AlertDialog(onDismissRequest = { confirmLogout = false },
            title = { Text("Kết thúc phiên làm việc?") },
            text = { Text("Các bản ghi đã lưu vẫn còn trên thiết bị. Bạn cần đăng nhập lại để tiếp tục.") },
            confirmButton = { TextButton(onClick = onLogoutClick) { Text("Đăng xuất") } },
            dismissButton = { TextButton(onClick = { confirmLogout = false }) { Text("Ở lại") } })
    }
}
