package com.project.cruise.android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.project.cruise.android.R
import com.project.cruise.android.data.dto.booking.BookingStatus
import com.project.cruise.android.data.dto.tour.TourBookingStatus
import com.project.cruise.android.ui.theme.*
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun passengerMoney(amount: BigDecimal?): String =
    amount?.let { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(it) }
        ?: "Chưa có giá"

fun passengerDate(raw: String?): String {
    if (raw.isNullOrBlank()) return "Chưa cập nhật"
    val date =
        runCatching { OffsetDateTime.parse(raw).toLocalDate() }.getOrNull()
            ?: runCatching { LocalDateTime.parse(raw).toLocalDate() }.getOrNull()
            ?: runCatching { LocalDate.parse(raw) }.getOrNull()
    return date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: raw
}

fun bookingLabel(status: BookingStatus?): String =
    when (status) {
        BookingStatus.PENDING_PAYMENT -> "Chờ thanh toán"
        BookingStatus.CONFIRMED -> "Đã xác nhận"
        BookingStatus.CANCELLED -> "Đã hủy"
        null -> "Chưa rõ trạng thái"
    }

fun passengerTime(raw: String?): String {
    if (raw.isNullOrBlank()) return "Chưa cập nhật"
    val time =
        runCatching { OffsetDateTime.parse(raw).toLocalTime() }.getOrNull()
            ?: runCatching { LocalDateTime.parse(raw).toLocalTime() }.getOrNull()
            ?: runCatching { LocalTime.parse(raw) }.getOrNull()
    return time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: raw
}

@Composable
fun PassengerDiscoveryHero() {
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(OceanNavy)) {
        Image(
            painterResource(R.drawable.ocean_welcome_hero),
            null,
            Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            Modifier.matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(OceanNavy.copy(alpha = .25f), OceanNavy.copy(alpha = .95f))
                    )
                )
        )
        Column(
            Modifier.padding(24.dp).heightIn(min = 210.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                "OCEANCRUISE · KHÁM PHÁ",
                style = MaterialTheme.typography.labelMedium,
                color = OceanMint,
            )
            Spacer(Modifier.height(34.dp))
            Text(
                "Kỳ nghỉ trên biển,\nbắt đầu từ đây.",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Text(
                "Tìm hành trình dành riêng cho bạn.",
                color = Color.White.copy(alpha = .9f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

fun tourSaleLabel(status: TourBookingStatus?): String =
    when (status) {
        TourBookingStatus.OPEN -> "Đang mở bán"
        TourBookingStatus.CLOSED -> "Đã đóng bán"
        TourBookingStatus.WAITING,
        TourBookingStatus.NOT_OPEN -> "Chưa mở bán"
        null -> "Chưa rõ trạng thái"
    }

enum class PassengerGlyph {
    EXPLORE,
    TICKET,
    PERSON,
    SEARCH,
    CALENDAR,
    ARROW,
    BACK,
    EXIT,
}

@Composable
fun PassengerIcon(
    glyph: PassengerGlyph,
    tint: Color = OceanTeal,
    modifier: Modifier = Modifier.size(24.dp),
) {
    Canvas(modifier) {
        val s = size.width / 24f
        fun p(x: Float, y: Float) = Offset(x * s, y * s)
        fun line(x: Float, y: Float, a: Float, b: Float) =
            drawLine(tint, p(x, y), p(a, b), 1.7f * s, StrokeCap.Round)
        fun box(x: Float, y: Float, w: Float, h: Float) =
            drawRoundRect(
                tint,
                p(x, y),
                androidx.compose.ui.geometry.Size(w * s, h * s),
                androidx.compose.ui.geometry.CornerRadius(2 * s),
                style = Stroke(1.7f * s),
            )
        when (glyph) {
            PassengerGlyph.SEARCH -> {
                drawCircle(tint, 6.5f * s, p(10f, 10f), style = Stroke(1.7f * s))
                line(15f, 15f, 21f, 21f)
            }
            PassengerGlyph.PERSON -> {
                drawCircle(tint, 3.5f * s, p(12f, 7f), style = Stroke(1.7f * s))
                drawArc(
                    tint,
                    180f,
                    180f,
                    false,
                    p(4f, 12f),
                    androidx.compose.ui.geometry.Size(16 * s, 16 * s),
                    style = Stroke(1.7f * s),
                )
            }
            PassengerGlyph.TICKET -> {
                box(3f, 5f, 18f, 14f)
                line(15f, 6f, 15f, 9f)
                line(15f, 11f, 15f, 13f)
                line(15f, 15f, 15f, 18f)
                line(6f, 10f, 11f, 10f)
                line(6f, 14f, 10f, 14f)
            }
            PassengerGlyph.CALENDAR -> {
                box(3f, 5f, 18f, 16f)
                line(3f, 10f, 21f, 10f)
                line(7f, 3f, 7f, 7f)
                line(17f, 3f, 17f, 7f)
                line(7f, 14f, 10f, 14f)
                line(14f, 17f, 17f, 17f)
            }
            PassengerGlyph.ARROW -> {
                line(4f, 12f, 20f, 12f)
                line(15f, 7f, 20f, 12f)
                line(15f, 17f, 20f, 12f)
            }
            PassengerGlyph.BACK -> {
                line(20f, 12f, 4f, 12f)
                line(9f, 7f, 4f, 12f)
                line(9f, 17f, 4f, 12f)
            }
            PassengerGlyph.EXIT -> {
                box(3f, 3f, 10f, 18f)
                line(9f, 12f, 21f, 12f)
                line(17f, 8f, 21f, 12f)
                line(17f, 16f, 21f, 12f)
            }
            PassengerGlyph.EXPLORE -> {
                drawCircle(tint, 9 * s, p(12f, 12f), style = Stroke(1.7f * s))
                val path =
                    Path().apply {
                        moveTo(16 * s, 8 * s)
                        lineTo(14 * s, 14 * s)
                        lineTo(8 * s, 16 * s)
                        lineTo(10 * s, 10 * s)
                        close()
                    }
                drawPath(path, tint, style = Stroke(1.7f * s))
            }
        }
    }
}

@Composable
fun PassengerPage(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {},
    content: LazyListScope.() -> Unit,
) {
    OceanTheme {
        Scaffold(
            containerColor = OceanSand,
            bottomBar = bottomBar,
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { padding ->
            Box(
                Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding(),
                contentAlignment = Alignment.TopCenter,
            ) {
                LazyColumn(
                    Modifier.widthIn(max = 600.dp).fillMaxWidth(),
                    contentPadding = PaddingValues(20.dp, 16.dp, 20.dp, 28.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            if (onBack != null)
                                FilledIconButton(
                                    onClick = onBack,
                                    modifier =
                                        Modifier.semantics { contentDescription = "Quay lại" },
                                    colors =
                                        IconButtonDefaults.filledIconButtonColors(
                                            containerColor = Color.White,
                                            contentColor = OceanNavy,
                                        ),
                                ) {
                                    PassengerIcon(PassengerGlyph.BACK, OceanNavy)
                                }
                            Column(
                                Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                            ) {
                                Text(
                                    title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = OceanNavy,
                                )
                                subtitle?.let {
                                    Text(
                                        it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OceanSlate,
                                    )
                                }
                            }
                        }
                    }
                    content()
                }
            }
        }
    }
}

@Composable
fun PassengerCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, OceanLine.copy(alpha = .55f)),
    ) {
        Column(
            Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content,
        )
    }
}

@Composable
fun PassengerPill(label: String, positive: Boolean = true) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = if (positive) OceanMintSoft else OceanLavender,
    ) {
        Text(
            label,
            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (positive) OceanTeal else OceanNavy,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun PassengerSection(title: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = OceanSlate) }
    }
}

@Composable
fun PassengerInfo(label: String, value: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = OceanSlate)
        Text(
            value?.takeIf { it.isNotBlank() } ?: "Chưa cập nhật",
            style = MaterialTheme.typography.bodyLarge,
            color = OceanNavy,
        )
    }
}

@Composable
fun PassengerLoading() {
    PassengerCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator(Modifier.size(28.dp), color = OceanTeal, strokeWidth = 3.dp)
            Text("Đang tải thông tin…", color = OceanSlate)
        }
    }
}

@Composable
fun PassengerEmpty(
    title: String,
    message: String,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    PassengerCard {
        Surface(color = OceanMintSoft, shape = RoundedCornerShape(18.dp)) {
            PassengerIcon(PassengerGlyph.TICKET, modifier = Modifier.padding(14.dp).size(30.dp))
        }
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        Text(message, color = OceanSlate)
        if (action != null && onAction != null) OceanPrimaryButton(action, onAction)
    }
}

@Composable
fun PassengerError(message: String, onRetry: () -> Unit) {
    PassengerCard {
        PassengerPill("Không tải được dữ liệu", false)
        Text(
            "Chưa tải được thông tin",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        Text(message, color = OceanSlate)
        OutlinedButton(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
        ) {
            Text("Thử lại")
        }
    }
}

@Composable
fun CruiseVisual(imageUrl: String?, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal)))
    ) {
        Canvas(Modifier.fillMaxSize()) {
            for (i in 0..5) {
                val path =
                    Path().apply {
                        moveTo(-30f, size.height * .68f + i * 18f)
                        cubicTo(
                            size.width * .3f,
                            size.height * .42f + i * 18f,
                            size.width * .7f,
                            size.height * .95f + i * 18f,
                            size.width + 30f,
                            size.height * .60f + i * 18f,
                        )
                    }
                drawPath(path, OceanMint.copy(alpha = .15f), style = Stroke(2f))
            }
        }
        ShipEmblem(Modifier.align(Alignment.Center).size(100.dp))
        if (!imageUrl.isNullOrBlank())
            AsyncImage(
                model = imageUrl,
                contentDescription = "Ảnh du thuyền",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
    }
}
