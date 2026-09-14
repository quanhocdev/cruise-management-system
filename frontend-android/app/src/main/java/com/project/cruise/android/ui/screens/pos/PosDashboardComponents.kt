package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.BuildConfig
import com.project.cruise.android.data.repository.PosTransactionQueue

internal data class PosDashboardAction(
    val title: String,
    val description: String,
    val symbol: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
    val status: String? = null
)

@Composable
internal fun RolePosDashboard(
    role: PosRole,
    accent: Color,
    actions: List<PosDashboardAction>,
    notice: String,
    onLogoutClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val queue = remember { PosTransactionQueue(context) }
    val pendingCount by queue.observePendingCount(role.apiRole).collectAsState(initial = 0)

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding()
            .padding(24.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onLogoutClick) { Text("← Đăng xuất POS") }
            Surface(color = accent.copy(alpha = .13f), shape = RoundedCornerShape(999.dp)) {
                Text(
                    role.apiRole,
                    Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    color = accent,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            role.title,
            modifier = Modifier.padding(top = 22.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(role.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            "Thiết bị ${BuildConfig.POS_TERMINAL_CODE}",
            modifier = Modifier.padding(top = 6.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )

        if (pendingCount > 0) {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                color = Color(0xFFFFF4DF),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    "$pendingCount lượt quét đang chờ đồng bộ",
                    Modifier.padding(14.dp),
                    color = Color(0xFF76551C)
                )
            }
        }

        Text(
            "Thao tác nghiệp vụ",
            modifier = Modifier.padding(top = 26.dp, bottom = 12.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        actions.forEach { action ->
            PosRoleActionCard(action, accent)
            Spacer(Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = onHistoryClick,
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Lịch sử quét và đồng bộ")
        }

        Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                notice,
                Modifier.padding(14.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PosRoleActionCard(action: PosDashboardAction, accent: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = action.enabled, onClick = action.onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (action.enabled) {
                MaterialTheme.colorScheme.surfaceContainerLow
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = .55f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (action.enabled) 2.dp else 0.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.background(accent.copy(alpha = .12f), RoundedCornerShape(14.dp)).padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(action.symbol, color = accent, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.padding(start = 16.dp).weight(1f)) {
                Text(action.title, fontWeight = FontWeight.Bold)
                Text(
                    action.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                action.status?.let {
                    Text(it, color = Color(0xFF9A650E), style = MaterialTheme.typography.labelSmall)
                }
            }
            Text(if (action.enabled) "›" else "—", style = MaterialTheme.typography.headlineSmall)
        }
    }
}
