package com.project.cruise.android.ui.passenger.components.feedback

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.passenger.feedback.FeedbackInvitationResponse
import com.project.cruise.android.data.repository.feedback.feedbackInvitationStatusLabel
import com.project.cruise.android.data.repository.feedback.feedbackTypeLabel
import com.project.cruise.android.ui.common.PassengerPill
import com.project.cruise.android.ui.common.PillTone
import com.project.cruise.android.ui.theme.*

enum class FeedbackStatus { PENDING, COMPLETED, SKIPPED, OTHER }

fun feedbackStatusOf(status: String?): FeedbackStatus =
    when (status?.uppercase()) {
        "PENDING" -> FeedbackStatus.PENDING
        "COMPLETED", "SUBMITTED" -> FeedbackStatus.COMPLETED
        "SKIPPED" -> FeedbackStatus.SKIPPED
        else -> FeedbackStatus.OTHER
    }

/**
 * Thẻ lời mời đánh giá, ba kiểu theo trạng thái:
 * - Đang chờ: nổi bật vàng, 5 sao chạm được để đánh giá nhanh, nút vàng "Đánh giá ngay".
 * - Đã đánh giá: thẻ trắng, dấu ✓ teal, nút xem chi tiết.
 * - Đã bỏ qua: thẻ mờ.
 * [onRate] nhận số sao đã chọn nhanh (5 nếu bấm nút).
 */
@Composable
fun FeedbackInvitationCard(
    invitation: FeedbackInvitationResponse,
    sending: Boolean,
    onRate: (Int) -> Unit,
    onSkip: () -> Unit,
    onViewDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val status = feedbackStatusOf(invitation.status)
    val pending = status == FeedbackStatus.PENDING

    Surface(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = if (pending) Color(0xFFFFFBEB) else Color.White,
        border = BorderStroke(if (pending) 1.5.dp else 1.dp, if (pending) OceanGold else OceanLine.copy(alpha = .6f)),
        shadowElevation = if (pending) 4.dp else 0.dp,
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            if (pending) Box(Modifier.width(5.dp).fillMaxHeight().background(OceanGold))
            Column(Modifier.weight(1f).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        feedbackTypeLabel(invitation.feedbackType),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (pending) OceanGoldDeep else OceanTeal,
                    )
                    PassengerPill(
                        feedbackInvitationStatusLabel(invitation.status),
                        when (status) {
                            FeedbackStatus.PENDING -> PillTone.WARNING
                            FeedbackStatus.COMPLETED -> PillTone.POSITIVE
                            else -> PillTone.NEUTRAL
                        },
                    )
                }

                Text(
                    invitation.targetName?.takeIf { it.isNotBlank() } ?: "Nội dung cần đánh giá",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (status == FeedbackStatus.SKIPPED) OceanNavy.copy(alpha = .6f) else OceanNavy,
                )
                invitation.bookingId?.let {
                    Text("Mã đặt chỗ: #$it", style = MaterialTheme.typography.bodySmall, color = OceanSlate)
                }

                when (status) {
                    FeedbackStatus.PENDING -> {
                        Text(
                            "Chạm vào sao để đánh giá nhanh",
                            style = MaterialTheme.typography.labelMedium,
                            color = OceanGoldDeep,
                        )
                        StarRatingRow(
                            rating = 0,
                            starSize = 34.dp,
                            inactiveColor = OceanGold.copy(alpha = .4f),
                            onRatingChange = { if (!sending) onRate(it) },
                        )
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Button(
                                onClick = { onRate(5) },
                                enabled = !sending,
                                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = OceanGold, contentColor = OceanNavy),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                            ) { Text("Đánh giá ngay", fontWeight = FontWeight.Bold) }
                            TextButton(onClick = onSkip, enabled = !sending) {
                                Text("Bỏ qua", color = OceanSlate, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    FeedbackStatus.COMPLETED -> Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.Check, null, tint = OceanTeal, modifier = Modifier.size(18.dp))
                            Text(
                                "Bạn đã đánh giá",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = OceanTeal,
                            )
                        }
                        OutlinedButton(
                            onClick = onViewDetail,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, OceanGold),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = OceanNavy),
                        ) { Text("Xem chi tiết", fontWeight = FontWeight.Bold) }
                    }
                    else -> Text(
                        "Lời mời này đã được bỏ qua.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OceanSlate,
                    )
                }
            }
        }
    }
}