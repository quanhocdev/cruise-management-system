package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
internal fun TourPage(backLabel: String, onBack: () -> Unit, content: LazyListScope.() -> Unit) {
    OceanTheme {
        Surface(Modifier.fillMaxSize(), color = OceanMist) {
            Column(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 20.dp)) {
                FilledTonalButton(onClick = onBack,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).heightIn(min = 56.dp)) {
                    Text("← $backLabel", style = MaterialTheme.typography.titleMedium)
                }
                LazyColumn(Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
            }
        }
    }
}

@Composable
internal fun TourCard(content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
internal fun TourSection(title: String) {
    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = OceanNavy)
}

internal fun LazyListScope.tourLoadingAndError(loading: Boolean, error: String?, retry: () -> Unit) {
    if (loading) item {
        LinearProgressIndicator(Modifier.fillMaxWidth())
        Text("Đang tải dữ liệu…", style = MaterialTheme.typography.bodySmall)
    }
    if (error != null) item {
        OceanNotice(error, true)
        OutlinedButton(onClick = retry, enabled = !loading, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Thử lại") }
    }
}
