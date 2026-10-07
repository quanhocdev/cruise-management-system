package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.cruise.android.BuildConfig
import com.project.cruise.android.data.local.pos.PosSyncStatus
import com.project.cruise.android.data.repository.PosTransactionQueue
import com.project.cruise.android.ui.theme.OceanTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal val PosBackground = Color(0xFFF5F6F8)
internal val PosInk = Color(0xFF102B46)
internal val PosMuted = Color(0xFF66758A)
internal val PosLine = Color(0xFFE7EBF0)
internal val PosAmber = Color(0xFF965716)
internal fun PosRole.accent() = when (this) {
    PosRole.FINANCE -> Color(0xFF007D80)
    PosRole.CONVENIENCE -> Color(0xFF7150B0)
    PosRole.ONBOARD -> Color(0xFFB75328)
    PosRole.SHORE -> Color(0xFF23764F)
}

internal enum class PosSymbol { SHIP, QR, NFC, KEYBOARD, HOME, HISTORY, PERSON, ARROW, BACK, CHECK, INFO, CLOCK, SEARCH, BAG, COMPASS, LOCK, CLOSE }

/** Icons use the same stroke and grid, independent of installed fonts. */
@Composable
internal fun PosGlyph(symbol: PosSymbol, color: Color = PosInk, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier) {
        scale(size.width / 24f, size.height / 24f, pivot = Offset.Zero) {
            fun line(x: Float, y: Float, xx: Float, yy: Float) = drawLine(color, Offset(x,y), Offset(xx,yy), 1.7f, StrokeCap.Round)
            fun rect(x: Float, y: Float, w: Float, h: Float) = drawRoundRect(color, Offset(x,y), Size(w,h), androidx.compose.ui.geometry.CornerRadius(1.5f), style = Stroke(1.7f))
            fun circle(x: Float, y: Float, r: Float) = drawCircle(color,r,Offset(x,y),style=Stroke(1.7f))
            when (symbol) {
                PosSymbol.QR -> { rect(3f,3f,6f,6f); rect(15f,3f,6f,6f); rect(3f,15f,6f,6f); rect(15f,15f,3f,3f); line(21f,14f,21f,18f); line(15f,21f,21f,21f); line(12f,3f,12f,6f); line(3f,12f,6f,12f); line(11f,12f,15f,12f) }
                PosSymbol.NFC -> { rect(3f,7f,7f,12f); line(5f,10f,8f,10f); drawArc(color,-55f,110f,false,Offset(7f,6f),Size(9f,12f),style=Stroke(1.7f)); drawArc(color,-55f,110f,false,Offset(8f,3f),Size(14f,18f),style=Stroke(1.7f)) }
                PosSymbol.KEYBOARD -> { rect(2f,5f,20f,14f); for(y in listOf(9f,12f)) for(x in listOf(6f,10f,14f,18f)) line(x,y,x+.2f,y); line(7f,16f,17f,16f) }
                PosSymbol.PERSON -> { circle(12f,7f,3.5f); drawArc(color,180f,180f,false,Offset(4f,13f),Size(16f,12f),style=Stroke(1.7f)); line(4f,19f,20f,19f) }
                PosSymbol.HOME -> { line(3f,10f,12f,3f); line(12f,3f,21f,10f); line(5f,9f,5f,21f); line(19f,9f,19f,21f); line(5f,21f,19f,21f); rect(10f,14f,4f,7f) }
                PosSymbol.HISTORY, PosSymbol.CLOCK -> { circle(12f,12f,9f); line(12f,6f,12f,12f); line(12f,12f,16f,14f) }
                PosSymbol.ARROW -> { line(4f,12f,20f,12f); line(14f,6f,20f,12f); line(14f,18f,20f,12f) }
                PosSymbol.BACK -> { line(20f,12f,4f,12f); line(10f,6f,4f,12f); line(10f,18f,4f,12f) }
                PosSymbol.CHECK -> { line(4f,12f,9f,17f); line(9f,17f,20f,6f) }
                PosSymbol.CLOSE -> { line(6f,6f,18f,18f); line(6f,18f,18f,6f) }
                PosSymbol.INFO -> { circle(12f,12f,9f); line(12f,11f,12f,17f); line(12f,7f,12f,7.2f) }
                PosSymbol.SEARCH -> { circle(10f,10f,6.5f); line(15f,15f,21f,21f) }
                PosSymbol.BAG -> { rect(4f,7f,16f,14f); drawArc(color,180f,180f,false,Offset(8f,1f),Size(8f,12f),style=Stroke(1.7f)) }
                PosSymbol.LOCK -> { rect(5f,10f,14f,11f); drawArc(color,180f,180f,false,Offset(8f,2f),Size(8f,15f),style=Stroke(1.7f)); line(12f,14f,12f,17f) }
                PosSymbol.COMPASS -> { circle(12f,12f,9f); val p=Path().apply { moveTo(8f,16f); lineTo(10f,10f); lineTo(16f,8f); lineTo(14f,14f); close() }; drawPath(p,color,style=Stroke(1.7f)) }
                PosSymbol.SHIP -> { line(12f,2f,12f,15f); line(12f,3f,6f,11f); line(6f,11f,12f,11f); line(15f,5f,19f,12f); line(19f,12f,15f,12f); val p=Path().apply { moveTo(3f,15f); lineTo(21f,15f); lineTo(18f,19f); lineTo(6f,19f); close() }; drawPath(p,color,style=Stroke(1.7f)); line(3f,22f,7f,21f); line(7f,21f,12f,22f); line(12f,22f,17f,21f); line(17f,21f,21f,22f) }
            }
        }
    }
}

@Composable
internal fun PosTheme(content: @Composable () -> Unit) {
    OceanTheme {
        MaterialTheme(colorScheme = lightColorScheme(
            primary = PosRole.FINANCE.accent(), onPrimary = Color.White,
            background = PosBackground, surface = Color.White,
            onSurface = PosInk, onSurfaceVariant = PosMuted,
            outline = PosLine, outlineVariant = PosLine,
            primaryContainer = Color(0xFFE0F1F1), onPrimaryContainer = PosInk,
            surfaceContainerLow = Color(0xFFEEF2F6)
        ), typography = MaterialTheme.typography.copy(
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontSize = 21.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold)
        )) {
            CompositionLocalProvider(LocalContentColor provides PosInk, content = content)
        }
    }
}

@Composable
internal fun PosBadge(text: String, color: Color) {
    Surface(color = color.copy(alpha = .10f), shape = RoundedCornerShape(8.dp)) {
        Text(text, Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = color,
            style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun PosPanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(Modifier.fillMaxWidth(), color = Color.White, shape = RoundedCornerShape(24.dp), border = BorderStroke(1.dp, PosLine)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
internal fun PosPage(content: @Composable ColumnScope.() -> Unit) {
    PosTheme {
        Surface(Modifier.fillMaxSize(), color = PosBackground) {
            Box(Modifier.safeDrawingPadding().imePadding(), contentAlignment = Alignment.TopCenter) {
                Column(Modifier.widthIn(max = 640.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp), content = content)
            }
        }
    }
}

@Composable
internal fun PosTopBar(title: String, role: PosRole, onBack: () -> Unit, enabled: Boolean = true) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FilledIconButton(onClick = onBack, enabled = enabled, modifier = Modifier.semantics { contentDescription = "Quay lại" }, colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White, contentColor = PosInk)) { PosGlyph(PosSymbol.BACK) }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(role.title, color = role.accent(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.titleLarge, color = PosInk)
        }
    }
}

@Composable
internal fun PosNotice(message: String, warning: Boolean = false) {
    val color = if (warning) PosAmber else PosMuted
    Surface(color = if(warning) Color(0xFFFFF3DF) else Color(0xFFEBF0F5), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PosGlyph(PosSymbol.INFO, color, Modifier.size(20.dp))
            Text(message, color = color, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
internal fun PosPrimaryButton(text: String, role: PosRole, onClick: () -> Unit, enabled: Boolean = true, loading: Boolean = false) {
    Button(onClick = onClick, enabled = enabled && !loading, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = role.accent())) {
        if (loading) CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
        else { Text(text, Modifier.weight(1f), fontWeight = FontWeight.Bold); PosGlyph(PosSymbol.ARROW, Color.White) }
    }
}

@Composable
internal fun PosEmptyState(title: String, message: String, symbol: PosSymbol = PosSymbol.HISTORY) {
    PosPanel {
        Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(64.dp).background(PosBackground, CircleShape), contentAlignment = Alignment.Center) { PosGlyph(symbol, PosMuted, Modifier.size(28.dp)) }
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(message, color = PosMuted, style = MaterialTheme.typography.bodyMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

internal data class PosDashboardAction(val title: String, val description: String, val symbol: String, val onClick: () -> Unit, val enabled: Boolean = true, val status: String? = null)
internal fun actionSymbol(symbol: String) = when(symbol) { "QR" -> PosSymbol.QR; "NFC" -> PosSymbol.NFC; "123" -> PosSymbol.KEYBOARD; else -> PosSymbol.ARROW }

@Composable
internal fun RolePosDashboard(username: String, role: PosRole, accent: Color, actions: List<PosDashboardAction>, notice: String, onLogoutClick: () -> Unit, onHistoryClick: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val queue = remember(context) { PosTransactionQueue(context) }
    val transactions by queue.observeAll(role.apiRole).collectAsState(initial = emptyList())
    val visible = transactions.filter { role == PosRole.FINANCE || it.scanType == (if(role == PosRole.CONVENIENCE) "NFC" else "QR") }
    val pendingCount = visible.count { it.status != PosSyncStatus.SYNCED.name && it.status != PosSyncStatus.CANCELLED.name }
    var account by rememberSaveable { mutableStateOf(false) }
    var confirmLogout by rememberSaveable { mutableStateOf(false) }
    PosTheme {
        Scaffold(containerColor = PosBackground, bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                listOf(Triple("Tác vụ", PosSymbol.HOME, 0), Triple("Lịch sử", PosSymbol.HISTORY, 1), Triple("Tài khoản", PosSymbol.PERSON, 2)).forEach { (label, icon, index) ->
                    val selected = if(index == 0) !account else index == 2 && account
                    NavigationBarItem(selected = selected, onClick = { when(index) { 0 -> account = false; 1 -> onHistoryClick(); else -> account = true } },
                        icon = { PosGlyph(icon, if(selected) accent else PosMuted) }, label = { Text(label, fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = accent.copy(alpha=.10f), selectedTextColor = accent, unselectedTextColor = PosMuted))
                }
            }
        }) { padding ->
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
                Column(Modifier.widthIn(max = 640.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.size(44.dp).background(Color.White, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { PosGlyph(PosSymbol.SHIP, accent, Modifier.size(28.dp)) }
                        Column(Modifier.weight(1f)) {
                            Text("CRUISE / POS", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                            Text("${role.apiRole} • Trạm phục vụ", style = MaterialTheme.typography.bodySmall, color = PosMuted)
                        }
                        IconButton(onClick = { account = !account }, modifier = Modifier.semantics { contentDescription = "Mở thông tin tài khoản" }) { PosGlyph(PosSymbol.PERSON, accent) }
                    }
                    PosSessionCard(username, role, account)
                    if (account) {
                        PosPanel {
                            Text("Thông tin phiên", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            PosDetailRow("Bộ phận", role.title)
                            PosDetailRow("Thiết bị", BuildConfig.POS_TERMINAL_CODE)
                            PosDetailRow("Bản ghi trên máy", visible.size.toString())
                            PosNotice("Đăng xuất để chuyển sang tài khoản nhân viên khác.")
                            OutlinedButton(onClick = { confirmLogout = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(14.dp)) { Text("Đăng xuất", color = MaterialTheme.colorScheme.error) }
                        }
                    } else {
                        if(pendingCount > 0) PosNotice("$pendingCount bản ghi chưa xác nhận. Kiểm tra trạng thái trong lịch sử.", warning = true)
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text("Bắt đầu phục vụ", style = MaterialTheme.typography.titleLarge)
                            Text(when(role) { PosRole.FINANCE -> "Tiếp nhận khách tại quầy lễ tân"; PosRole.CONVENIENCE -> "Chạm vòng tay để bắt đầu kiểm tra"; PosRole.ONBOARD -> "Kiểm tra mã vé của hành khách"; PosRole.SHORE -> "Đối chiếu khách và chuyến tham quan" }, color = PosMuted, style = MaterialTheme.typography.bodyMedium)
                        }
                        actions.filter { it.enabled }.forEachIndexed { index, action -> PosActionCard(action, accent, prominent = index == 0) }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PosMetric(visible.size.toString(), "Bản ghi trên máy", accent, Modifier.weight(1f))
                            PosMetric(visible.count { it.status == PosSyncStatus.SYNCED.name }.toString(), "Đã gửi máy chủ", accent, Modifier.weight(1f))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Thao tác gần đây", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            TextButton(onClick = onHistoryClick) { Text("Xem tất cả", color = accent) }
                        }
                        if(visible.isEmpty()) PosEmptyState("Sẵn sàng cho lượt đầu tiên", "Lượt quét đã lưu sẽ xuất hiện tại đây.")
                        else visible.sortedByDescending { it.createdAt }.take(2).forEach { transaction ->
                            Surface(onClick = onHistoryClick, color = Color.White, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, PosLine)) {
                                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    PosGlyph(if(transaction.scanType == "NFC") PosSymbol.NFC else PosSymbol.QR, accent)
                                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(posDisplayCode(transaction.scannedValue), maxLines=1, overflow=TextOverflow.Ellipsis, fontWeight=FontWeight.SemiBold)
                                        Text(SimpleDateFormat("HH:mm · dd/MM", Locale.getDefault()).format(Date(transaction.createdAt)),color=PosMuted,style=MaterialTheme.typography.bodySmall)
                                    }
                                    PosBadge(when(transaction.status) {
                                        PosSyncStatus.SYNCED.name -> "Đã gửi"
                                        PosSyncStatus.SYNCING.name -> "Đang gửi"
                                        PosSyncStatus.FAILED.name -> "Cần kiểm tra"
                                        PosSyncStatus.CANCELLED.name -> "Đã hủy"
                                        else -> "Đã lưu"
                                    }, if(transaction.status == PosSyncStatus.SYNCED.name) accent else PosAmber)
                                }
                            }
                        }
                        PosNotice(notice)
                    }
                }
            }
        }
        if (confirmLogout) AlertDialog(onDismissRequest = { confirmLogout = false }, title = { Text("Kết thúc phiên làm việc?") }, text = { Text("Các bản ghi đã lưu vẫn còn trên thiết bị. Đăng nhập lại để tiếp tục phục vụ.") },
            confirmButton = { TextButton(onClick = onLogoutClick) { Text("Đăng xuất") } }, dismissButton = { TextButton(onClick = { confirmLogout = false }) { Text("Ở lại") } })
    }
}

@Composable
private fun PosSessionCard(username: String, role: PosRole, account: Boolean) {
    Surface(color = PosInk, shape = RoundedCornerShape(26.dp)) {
        Box(Modifier.fillMaxWidth()) {
            Canvas(Modifier.matchParentSize()) {
                drawCircle(role.accent().copy(alpha=.55f), size.width*.44f, Offset(size.width*.98f,size.height*.9f))
                drawCircle(Color.White.copy(alpha=.07f), size.width*.34f, Offset(size.width*.98f,size.height*.9f),style=Stroke(24f))
            }
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(if(account) "TÀI KHOẢN NHÂN VIÊN" else "PHIÊN LÀM VIỆC", color = Color(0xFFB8CCD9), style = MaterialTheme.typography.labelSmall, letterSpacing = 2.sp)
                Text(username.ifBlank { "Nhân viên" }, color = Color.White, style = MaterialTheme.typography.headlineMedium)
                Text(role.title, color = Color.White.copy(alpha=.85f), style = MaterialTheme.typography.bodyMedium)
                HorizontalDivider(color = Color.White.copy(alpha=.15f))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PosGlyph(PosSymbol.SHIP, Color(0xFFBEE6E5), Modifier.size(18.dp))
                    Text(BuildConfig.POS_TERMINAL_CODE, color = Color(0xFFBEE6E5), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun PosMetric(value: String, label: String, accent: Color, modifier: Modifier) {
    Surface(modifier, color = Color.White, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, PosLine)) {
        Column(Modifier.padding(18.dp), verticalArrangement=Arrangement.spacedBy(6.dp)) {
            Text(value, style=MaterialTheme.typography.headlineMedium, color=accent)
            Text(label, color=PosMuted,style=MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun PosActionCard(action: PosDashboardAction, accent: Color, prominent: Boolean) {
    Surface(onClick=action.onClick, modifier=Modifier.fillMaxWidth(), color=if(prominent) accent else Color.White,
        shape=RoundedCornerShape(22.dp), border=if(prominent) null else BorderStroke(1.dp,PosLine), shadowElevation=if(prominent) 3.dp else 0.dp) {
        Row(Modifier.padding(if(prominent) 22.dp else 18.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)) {
            Box(Modifier.size(if(prominent) 56.dp else 44.dp).background(if(prominent) Color.White.copy(alpha=.16f) else accent.copy(alpha=.08f),RoundedCornerShape(15.dp)),contentAlignment=Alignment.Center) {
                PosGlyph(actionSymbol(action.symbol),if(prominent) Color.White else accent,Modifier.size(if(prominent) 32.dp else 24.dp))
            }
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                Text(action.title,color=if(prominent) Color.White else PosInk,style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold)
                Text(action.description,color=if(prominent) Color.White.copy(alpha=.85f) else PosMuted,style=MaterialTheme.typography.bodySmall)
            }
            PosGlyph(PosSymbol.ARROW,if(prominent) Color.White else accent,Modifier.size(20.dp))
        }
    }
}

@Composable
internal fun PosDetailRow(label: String, value: String) {
    Column(verticalArrangement=Arrangement.spacedBy(4.dp)) {
        Text(label,color=PosMuted,style=MaterialTheme.typography.labelMedium)
        Text(value,color=PosInk,fontWeight=FontWeight.SemiBold)
    }
}

internal fun posDisplayCode(value: String): String = when {
    value.startsWith("POS:") -> "QR định danh · đã ẩn mã"
    value.length > 24 -> "${value.take(8)}••••${value.takeLast(6)}"
    else -> value
}
