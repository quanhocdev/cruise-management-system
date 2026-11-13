package com.project.cruise.android.ui.auth.components.otp

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.cruise.android.ui.theme.*

private const val OTP_LENGTH = 6

/** Ô nhập OTP 6 số. Chạm vào bất kỳ ô nào cũng focus lại và hiện bàn phím. */
@Composable
fun OtpCodeField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(enabled) { if (enabled) runCatching { focusRequester.requestFocus() } }

    BasicTextField(
        value = value,
        onValueChange = { raw -> onValueChange(raw.filter(Char::isDigit).take(OTP_LENGTH)) },
        modifier = modifier.focusRequester(focusRequester),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        cursorBrush = SolidColor(Color.Transparent),
        decorationBox = { innerTextField ->
            Box(
                Modifier.fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = enabled,
                    ) {
                        runCatching { focusRequester.requestFocus() }
                    }
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(OTP_LENGTH) { index ->
                        OtpDigitBox(
                            digit = value.getOrNull(index)?.toString().orEmpty(),
                            isActive = enabled && index == value.length.coerceAtMost(OTP_LENGTH - 1) &&
                                    value.length < OTP_LENGTH,
                            isError = isError,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                // Ô nhập thật phải được compose để nhận focus/bàn phím, nhưng ẩn đi.
                Box(Modifier.size(1.dp).alpha(0f)) { innerTextField() }
            }
        },
    )
}

@Composable
private fun OtpDigitBox(
    digit: String,
    isActive: Boolean,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    val borderColor by
    animateColorAsState(
        targetValue =
            when {
                isError -> MaterialTheme.colorScheme.error
                isActive -> OceanTeal
                digit.isNotEmpty() -> OceanTeal.copy(alpha = .45f)
                else -> OceanLine
            },
        label = "otpBorder",
    )
    val containerColor by
    animateColorAsState(
        targetValue = if (isActive) OceanMintSoft else if (digit.isNotEmpty()) Color.White else OceanPearl,
        label = "otpContainer",
    )

    Surface(
        modifier = modifier.heightIn(min = 58.dp),
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(if (isActive) 2.dp else 1.dp, borderColor),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(digit, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = OceanNavy)
        }
    }
}