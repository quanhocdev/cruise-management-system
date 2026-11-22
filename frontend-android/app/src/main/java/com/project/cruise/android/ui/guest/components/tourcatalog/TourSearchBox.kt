package com.project.cruise.android.ui.guest.components.tourcatalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.cruise.android.ui.theme.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourSearchBox(
    criteria: TourSearchCriteria,
    onCriteriaChange: (TourSearchCriteria) -> Unit,
    onSearchClick: () -> Unit,
    onBrowseAllClick: (() -> Unit)? = null,
    searchButtonLabel: String = "Tìm tour",
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatted = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                                .format(DateTimeFormatter.ISO_LOCAL_DATE)
                            onCriteriaChange(criteria.copy(date = formatted))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Chọn", fontWeight = FontWeight.Bold, color = OceanNavy)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Hủy", color = OceanSlate)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OceanNavy.copy(alpha = 0.08f)),
        shadowElevation = 6.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Hàng 1: Điểm đi <-> Điểm đến nằm ngang gọn gàng
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF4F8FB),
                border = BorderStroke(1.dp, OceanTeal.copy(alpha = 0.18f)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    CompactPortInput(
                        label = "ĐIỂM ĐI",
                        placeholder = "Cảng / TP đi",
                        value = criteria.departure,
                        icon = Icons.Default.FlightTakeoff,
                        onValueChange = { onCriteriaChange(criteria.copy(departure = it)) },
                        modifier = Modifier.weight(1f),
                    )

                    Surface(
                        onClick = {
                            onCriteriaChange(
                                criteria.copy(
                                    departure = criteria.destination,
                                    destination = criteria.departure,
                                )
                            )
                        },
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(1.dp, OceanTeal.copy(alpha = 0.25f)),
                        modifier = Modifier.size(32.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Đảo chiều",
                                tint = OceanTeal,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }

                    CompactPortInput(
                        label = "ĐIỂM ĐẾN",
                        placeholder = "Cảng / TP đến",
                        value = criteria.destination,
                        icon = Icons.Default.FlightLand,
                        onValueChange = { onCriteriaChange(criteria.copy(destination = it)) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Hàng 2: Chọn ngày khởi hành + Nút Tìm kiếm
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    onClick = { showDatePicker = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF4F8FB),
                    border = BorderStroke(1.dp, OceanTeal.copy(alpha = 0.18f)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = OceanTeal,
                            modifier = Modifier.size(18.dp),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "NGÀY KHỞI HÀNH",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = OceanSlate,
                                maxLines = 1,
                            )
                            Text(
                                text = criteria.date.ifBlank { "Chọn ngày" },
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (criteria.date.isBlank()) FontWeight.Normal else FontWeight.SemiBold,
                                color = if (criteria.date.isBlank()) OceanSlate.copy(alpha = 0.7f) else OceanNavy,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        if (criteria.date.isNotBlank()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Xóa ngày",
                                tint = OceanSlate,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .clickable { onCriteriaChange(criteria.copy(date = "")) },
                            )
                        }
                    }
                }

                Button(
                    onClick = onSearchClick,
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OceanNavy,
                        contentColor = Color.White,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = searchButtonLabel,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Hàng 3 (tùy chọn cho trang Khám phá): Link xem toàn bộ tour gọn gàng
            if (onBrowseAllClick != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(OceanMint.copy(alpha = 0.14f))
                        .clickable(onClick = onBrowseAllClick)
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Xem tất cả các tour hiện tại",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = OceanNavy,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = OceanNavy,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactPortInput(
    label: String,
    placeholder: String,
    value: String,
    icon: ImageVector,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OceanTeal,
                modifier = Modifier.size(13.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                fontWeight = FontWeight.SemiBold,
                color = OceanSlate,
                maxLines = 1,
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = OceanNavy,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            cursorBrush = SolidColor(OceanNavy),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = TextStyle(
                                    color = OceanSlate.copy(alpha = 0.65f),
                                    fontSize = 13.sp,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        innerTextField()
                    }
                    if (value.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Xóa",
                            tint = OceanSlate,
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .clickable { onValueChange("") },
                        )
                    }
                }
            },
        )
    }
}