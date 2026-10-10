package com.project.cruise.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun QuantityStepper(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    min: Int = 1
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedButton(
            onClick = { if (quantity > min) onQuantityChange(quantity - 1) },
            enabled = enabled
        ) {
            Text("−")
        }

        Text(
            text = quantity.toString(),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedButton(
            onClick = { onQuantityChange(quantity + 1) },
            enabled = enabled
        ) {
            Text("+")
        }
    }
}