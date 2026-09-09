package com.project.cruise.android.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription

@Composable
fun OceanTheme(content: @Composable () -> Unit) {
    CruiseManagementTheme(content = content)
}

@Composable
fun OceanPage(content: @Composable ColumnScope.() -> Unit) {
    OceanTheme {
        Surface(Modifier.fillMaxSize(), color = OceanMist) {
            Box(Modifier.fillMaxSize().safeDrawingPadding().imePadding(), contentAlignment = Alignment.TopCenter) {
                Column(Modifier.widthIn(max = 560.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp), content = content)
            }
        }
    }
}

@Composable
fun OceanHeader(title: String, onBack: (() -> Unit)? = null, enabled: Boolean = true) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) FilledTonalButton(onClick = onBack, enabled = enabled,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            modifier = Modifier.heightIn(min = 48.dp).semantics { contentDescription = "Quay lại" }) {
            Text("←", fontSize = 22.sp)
        }
        Spacer(Modifier.width(12.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
    }
}

@Composable
fun ShipEmblem(modifier: Modifier = Modifier) {
    Canvas(modifier.size(64.dp).background(OceanNavy, RoundedCornerShape(20.dp)).padding(10.dp)) {
        val w = size.width; val h = size.height
        drawRect(OceanTeal, Offset(w * .3f, h * .23f), Size(w * .4f, h * .1f))
        drawRect(Color(0xFFFF665F), Offset(w * .5f, h * .09f), Size(w * .13f, h * .1f))
        val deck = Path().apply { moveTo(w*.24f,h*.37f); lineTo(w*.76f,h*.37f); lineTo(w*.86f,h*.53f); lineTo(w*.14f,h*.53f); close() }
        drawPath(deck, Color.White)
        val hull = Path().apply { moveTo(w*.1f,h*.58f); lineTo(w*.9f,h*.58f); lineTo(w*.8f,h*.76f); lineTo(w*.2f,h*.76f); close() }
        drawPath(hull, Color.White)
        for (i in 0..3) drawCircle(OceanNavy, w*.028f, Offset(w*(.3f+.13f*i),h*.66f))
        val wave = Path().apply { moveTo(w*.08f,h*.86f); cubicTo(w*.3f,h*.97f,w*.65f,h*.78f,w*.93f,h*.88f) }
        drawPath(wave, OceanMint, style = androidx.compose.ui.graphics.drawscope.Stroke(width = w*.04f))
    }
}

@Composable
fun OceanBanner(eyebrow: String, title: String, subtitle: String? = null, dark: Boolean = true) {
    Surface(shape = RoundedCornerShape(24.dp), color = if (dark) OceanNavy else OceanLavender) {
        Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(eyebrow, color = if (dark) OceanMint else OceanTeal, style = MaterialTheme.typography.labelLarge)
                Text(title, color = if (dark) Color.White else OceanNavy, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                subtitle?.let { Text(it, color = if (dark) Color.White else MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            ShipEmblem()
        }
    }
}

@Composable
fun OceanNotice(text: String, error: Boolean = false) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        color = if (error) MaterialTheme.colorScheme.errorContainer else OceanLavender) {
        Text(text, Modifier.padding(16.dp), color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun OceanStatePanel(
    title: String,
    message: String,
    symbol: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    error: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = if (error) MaterialTheme.colorScheme.errorContainer else Color.White,
        tonalElevation = 1.dp
    ) {
        Column(
            Modifier.padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(symbol, fontSize = 34.sp)
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (actionLabel != null && onAction != null) {
                OutlinedButton(onClick = onAction, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text(actionLabel)
                }
            }
        }
    }
}

@Composable
fun OceanPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun OceanAction(title: String, subtitle: String, symbol: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(shape = RoundedCornerShape(16.dp), color = OceanLavender) {
                Text(symbol, Modifier.padding(16.dp), color = OceanTeal, fontSize = 24.sp)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("›", color = OceanTeal, fontSize = 28.sp)
        }
    }
}
