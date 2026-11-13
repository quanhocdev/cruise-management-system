package com.project.cruise.android.ui.auth.components.authform

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.guest.components.guestwelcome.pressScale
import com.project.cruise.android.ui.theme.*

/** Đường kẻ có chữ "hoặc" ở giữa. */
@Composable
fun AuthDivider(modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HorizontalDivider(Modifier.weight(1f), color = OceanLine)
        Text("hoặc", style = MaterialTheme.typography.labelMedium, color = OceanSlate)
        HorizontalDivider(Modifier.weight(1f), color = OceanLine)
    }
}

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val source = remember { MutableInteractionSource() }
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        interactionSource = source,
        modifier = modifier.fillMaxWidth().heightIn(min = 54.dp).pressScale(source),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, OceanLine),
        colors =
            ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = OceanNavy),
    ) {
        Box(
            Modifier.size(26.dp).clip(CircleShape).background(OceanPearl),
            contentAlignment = Alignment.Center,
        ) {
            Text("G", fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
        }
        Spacer(Modifier.width(12.dp))
        Text("Tiếp tục với Google", fontWeight = FontWeight.SemiBold)
    }
}