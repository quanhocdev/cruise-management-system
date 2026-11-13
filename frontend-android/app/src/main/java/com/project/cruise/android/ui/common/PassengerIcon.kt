package com.project.cruise.android.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

enum class PassengerGlyph {
    EXPLORE,
    TICKET,
    PERSON,
    MESSAGE,
    SEARCH,
    CALENDAR,
    ARROW,
    BACK,
    EXIT,
    MAP,      // Đã thêm biểu tượng MAP dùng cho icon Giám sát
    COMPASS   // Đã thêm biểu tượng COMPASS la bàn tùy chọn
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
                Size(w * s, h * s),
                CornerRadius(2f * s),
                style = Stroke(1.7f * s),
            )
        when (glyph) {
            PassengerGlyph.MESSAGE -> {
                drawRoundRect(
                    color = tint,
                    topLeft = p(3f, 4f),
                    size = Size(18f * s, 14f * s),
                    cornerRadius = CornerRadius(4f * s),
                    style = Stroke(1.7f * s),
                )
                line(7f, 18f, 4f, 21f)
                line(4f, 21f, 4f, 15f)
                drawCircle(tint, 1f * s, p(8f, 11f))
                drawCircle(tint, 1f * s, p(12f, 11f))
                drawCircle(tint, 1f * s, p(16f, 11f))
            }
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
                    Size(16f * s, 16f * s),
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
                drawCircle(tint, 9f * s, p(12f, 12f), style = Stroke(1.7f * s))
                val path = Path().apply {
                    moveTo(16f * s, 8f * s)
                    lineTo(14f * s, 14f * s)
                    lineTo(8f * s, 16f * s)
                    lineTo(10f * s, 10f * s)
                    close()
                }
                drawPath(path, tint, style = Stroke(1.7f * s))
            }
            PassengerGlyph.MAP -> {
                // Vẽ biểu tượng bản đồ / định vị cho tính năng Giám sát tàu
                box(3f, 4f, 18f, 16f)
                line(8f, 4f, 8f, 20f)
                line(16f, 4f, 16f, 20f)
            }
            PassengerGlyph.COMPASS -> {
                // Vẽ biểu tượng la bàn thay thế tùy chọn
                drawCircle(tint, 9f * s, p(12f, 12f), style = Stroke(1.7f * s))
                line(12f, 7f, 12f, 17f)
                line(7f, 12f, 17f, 12f)
            }
        }
    }
}