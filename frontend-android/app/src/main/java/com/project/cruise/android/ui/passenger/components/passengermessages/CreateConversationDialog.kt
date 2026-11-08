package com.project.cruise.android.ui.passenger.components.passengermessages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.chat.ConfirmedBookingResponse
import com.project.cruise.android.ui.theme.*

@Composable
fun CreateConversationDialog(
    bookings: List<ConfirmedBookingResponse>,
    onDismiss: () -> Unit,
    onCreate: (Long, String) -> Unit
) {
    var selectedBooking by remember { mutableStateOf<ConfirmedBookingResponse?>(null) }
    var selectedDepartment by remember { mutableStateOf("ONBOARD") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = {
            Text(
                "Tạo cuộc trò chuyện",
                fontWeight = FontWeight.Bold,
                color = OceanNavy
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                DropdownField(
                    label = "Booking đã xác nhận",
                    valueText = selectedBooking?.let { it.bookingCode ?: "Booking #${it.bookingId}" },
                    placeholder = "Chọn booking",
                    items = bookings,
                    itemLabel = { it.bookingCode ?: "Booking #${it.bookingId}" },
                    onSelect = { selectedBooking = it }
                )

                DropdownField(
                    label = "Phòng ban",
                    valueText = departmentLabel(selectedDepartment),
                    placeholder = "Chọn phòng ban",
                    items = MessageDepartments,
                    itemLabel = { it.second },
                    onSelect = { selectedDepartment = it.first }
                )

                if (bookings.isEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Bạn chưa có booking đã xác nhận.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = selectedBooking != null,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OceanTeal,
                    contentColor = Color.White
                ),
                onClick = {
                    selectedBooking?.let { onCreate(it.bookingId, selectedDepartment) }
                }
            ) {
                Text("Tạo", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = OceanSlate)
            }
        }
    )
}

@Composable
private fun <T> DropdownField(
    label: String,
    valueText: String?,
    placeholder: String,
    items: List<T>,
    itemLabel: (T) -> String,
    onSelect: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = OceanSlate)

        Box {
            Surface(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, if (expanded) OceanTeal else OceanLine)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = valueText ?: placeholder,
                        modifier = Modifier.weight(1f),
                        color = if (valueText != null) OceanNavy else OceanSlate,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = OceanSlate
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(itemLabel(item), color = OceanNavy) },
                        onClick = {
                            onSelect(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}