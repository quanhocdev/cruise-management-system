package com.project.cruise.android.ui.guest.components.tourdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.common.passengerMoney
import com.project.cruise.android.ui.passenger.components.mybookingdetail.GoldBadge
import com.project.cruise.android.ui.theme.*
import java.math.BigDecimal

private const val VISIBLE_BENEFITS = 3

/**
 * Thẻ gói tour dạng băng chuyền: đầu thẻ nền navy, giá vàng to; quyền lợi chỉ hiện 3 dòng đầu,
 * còn lại núp sau nút "Xem tất cả".
 */
@Composable
fun TourPackageCard(
    name: String,
    description: String?,
    maxPassengers: Number?,
    price: BigDecimal?,
    benefits: List<String>,
    isLoggedIn: Boolean,
    canBook: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAll by rememberSaveable { mutableStateOf(false) }
    val extra = (benefits.size - VISIBLE_BENEFITS).coerceAtLeast(0)

    Surface(
        modifier,
        shape = RoundedCornerShape(26.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, OceanGold),
        shadowElevation = 4.dp,
    ) {
        Column {
            Column(
                Modifier.fillMaxWidth()
                    .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal)))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                maxPassengers?.let { GoldBadge("Tối đa $it khách / phòng") }
                Spacer(Modifier.height(2.dp))
                Text("Giá mỗi phòng", style = MaterialTheme.typography.labelMedium, color = OceanMint)
                Text(
                    passengerMoney(price),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = OceanGold,
                )
            }

            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                description?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OceanSlate,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                if (benefits.isNotEmpty()) {
                    HorizontalDivider(color = OceanLine)
                    Text("Quyền lợi đi kèm", fontWeight = FontWeight.Bold, color = OceanNavy)
                    benefits.take(VISIBLE_BENEFITS).forEach { BenefitLine(it) }
                    if (extra > 0) {
                        AnimatedVisibility(showAll) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                benefits.drop(VISIBLE_BENEFITS).forEach { BenefitLine(it) }
                            }
                        }
                        Text(
                            if (showAll) "Thu gọn" else "Xem tất cả $extra quyền lợi khác",
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { showAll = !showAll }
                                .padding(vertical = 2.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = OceanGoldDeep,
                        )
                    }
                }

                Button(
                    onClick = onSelect,
                    enabled = canBook,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OceanGold,
                        contentColor = OceanNavy,
                        disabledContainerColor = OceanLine,
                        disabledContentColor = OceanSlate,
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                ) {
                    Text(
                        if (isLoggedIn) "Chọn gói này" else "Đăng nhập để đặt tour",
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (!canBook) {
                    Text(
                        "Hành trình hiện chưa nhận đặt chỗ.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OceanSlate,
                    )
                }
            }
        }
    }
}

@Composable
private fun BenefitLine(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        Box(
            Modifier.padding(top = 2.dp).size(18.dp).clip(CircleShape).background(OceanGold),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Filled.Check, null, tint = OceanNavy, modifier = Modifier.size(12.dp)) }
        Text(text, style = MaterialTheme.typography.bodyMedium, color = OceanNavy)
    }
}