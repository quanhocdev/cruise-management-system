package com.project.cruise.android.ui.passenger.components.booking

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*
import java.time.LocalDate
import java.time.ZoneId

/** [dateOfBirth] dạng ISO (yyyy-MM-dd) hoặc rỗng; [onPick] trả về chuỗi ISO. */
@Composable
fun DateOfBirthField(
    dateOfBirth: String,
    enabled: Boolean,
    onPick: (String) -> Unit,
) {
    val context = LocalContext.current

    Surface(
        onClick = {
            val initial =
                runCatching { LocalDate.parse(dateOfBirth) }
                    .getOrDefault(LocalDate.now().minusYears(18))
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    onPick(LocalDate.of(year, month + 1, day).toString())
                },
                initial.year,
                initial.monthValue - 1,
                initial.dayOfMonth,
            )
                .apply {
                    datePicker.maxDate =
                        LocalDate.now()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli() - 1
                }
                .show()
        },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OceanLine),
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Filled.DateRange, contentDescription = null, tint = OceanTeal)
            Text(
                if (dateOfBirth.isBlank()) "Chọn ngày sinh *"
                else "Ngày sinh: ${passengerDate(dateOfBirth)}",
                style = MaterialTheme.typography.bodyLarge,
                color = if (dateOfBirth.isBlank()) OceanSlate else OceanNavy,
            )
        }
    }
}