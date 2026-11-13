package com.project.cruise.android.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

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
                    item { PageHeader(title, subtitle, onBack) }
                    content()
                }
            }
        }
    }
}

@Composable
private fun PageHeader(title: String, subtitle: String?, onBack: (() -> Unit)?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (onBack != null)
            Surface(
                onClick = onBack,
                modifier =
                    Modifier.size(46.dp).semantics { contentDescription = "Quay lại" },
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(1.dp, OceanLine),
                shadowElevation = 2.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    PassengerIcon(PassengerGlyph.BACK, OceanNavy, Modifier.size(22.dp))
                }
            }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = OceanNavy,
            )
            subtitle?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelMedium,
                    color = OceanTeal,
                )
            }
        }
    }
}