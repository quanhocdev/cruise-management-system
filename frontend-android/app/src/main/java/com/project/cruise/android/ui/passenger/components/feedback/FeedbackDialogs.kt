package com.project.cruise.android.ui.passenger.components.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

/** Hộp thoại gửi đánh giá: sao to chạm được, nhãn cảm xúc vàng, ô nhận xét, nút gửi vàng. */
@Composable
fun FeedbackSubmitDialog(
    typeLabel: String,
    targetName: String,
    rating: Int,
    onRatingChange: (Int) -> Unit,
    comment: String,
    onCommentChange: (String) -> Unit,
    sending: Boolean,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { if (!sending) onDismiss() },
        containerColor = Color.White,
        shape = RoundedCornerShape(28.dp),
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(typeLabel, style = MaterialTheme.typography.labelLarge, color = OceanGoldDeep, fontWeight = FontWeight.Bold)
                Text(targetName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = OceanNavy)
            }
        },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(OceanGoldSoft).padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    StarRatingRow(rating = rating, starSize = 40.dp, onRatingChange = onRatingChange)
                    Text(
                        ratingLabel(rating),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = OceanGoldDeep,
                        textAlign = TextAlign.Center,
                    )
                }
                OutlinedTextField(
                    value = comment,
                    onValueChange = { if (it.length <= 2000) onCommentChange(it) },
                    label = { Text("Nhận xét của bạn") },
                    supportingText = { Text("${comment.length}/2000") },
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OceanGold,
                        focusedLabelColor = OceanGoldDeep,
                        cursorColor = OceanGoldDeep,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = !sending && rating in 1..5,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OceanGold, contentColor = OceanNavy),
            ) { Text(if (sending) "Đang gửi..." else "Gửi đánh giá", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !sending) { Text("Hủy", color = OceanSlate) }
        },
    )
}

/** Hộp thoại xem lại đánh giá đã gửi: sao vàng chỉ đọc, nhận xét trong khung vàng nhạt. */
@Composable
fun FeedbackDetailDialog(
    loading: Boolean,
    found: Boolean,
    typeLabel: String,
    targetName: String,
    rating: Int,
    comment: String?,
    bookingId: Any?,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(28.dp),
        title = { Text("Đánh giá của bạn", fontWeight = FontWeight.Bold, color = OceanNavy) },
        text = {
            when {
                loading -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    CircularProgressIndicator(Modifier.size(20.dp), color = OceanGold, strokeWidth = 2.5.dp)
                    Text("Đang tải đánh giá...", color = OceanSlate)
                }
                found -> Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(typeLabel, style = MaterialTheme.typography.labelLarge, color = OceanGoldDeep, fontWeight = FontWeight.Bold)
                    Text(targetName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OceanNavy)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StarRatingRow(rating = rating, starSize = 28.dp)
                        Text("$rating/5", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OceanGoldDeep)
                    }
                    Surface(shape = RoundedCornerShape(16.dp), color = OceanGoldSoft, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            comment?.trim()?.takeIf { it.isNotEmpty() } ?: "Không có nhận xét.",
                            Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = OceanNavy,
                        )
                    }
                    bookingId?.let {
                        Text("Mã đặt chỗ: #$it", style = MaterialTheme.typography.bodySmall, color = OceanSlate)
                    }
                }
                else -> Text("Không tìm thấy nội dung đánh giá.", color = OceanSlate)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OceanNavy, contentColor = OceanGold),
            ) { Text("Đóng", fontWeight = FontWeight.Bold) }
        },
    )
}