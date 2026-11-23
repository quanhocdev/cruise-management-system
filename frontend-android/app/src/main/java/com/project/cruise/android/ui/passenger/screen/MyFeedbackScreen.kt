package com.project.cruise.android.ui.passenger.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.passenger.feedback.FeedbackInvitationResponse
import com.project.cruise.android.data.repository.feedback.feedbackInvitationStatusLabel
import com.project.cruise.android.data.repository.feedback.feedbackTypeLabel
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.theme.OceanBanner
import com.project.cruise.android.viewmodel.passenger.FeedbackFilterKey
import com.project.cruise.android.viewmodel.passenger.FeedbackViewModel

@Composable
fun MyFeedbackScreen(model: FeedbackViewModel, onBack: () -> Unit) {
    val state by model.state.collectAsState()

    // State phục vụ mở Dialog đánh giá cho một invitation cụ thể
    var selectedInvitation by remember { mutableStateOf<FeedbackInvitationResponse?>(null) }
    var rating by rememberSaveable { mutableStateOf(5) }
    var comment by rememberSaveable { mutableStateOf("") }
    var showSubmitDialog by remember { mutableStateOf(false) }

    // State phục vụ mở Dialog xem chi tiết đánh giá đã gửi
    var showDetailDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        model.loadInvitations()
    }

    val counts = remember(state.invitations) {
        mapOf(
            FeedbackFilterKey.ALL to state.invitations.size,
            FeedbackFilterKey.PENDING to state.invitations.count { it.status.equals("PENDING", ignoreCase = true) },
            FeedbackFilterKey.COMPLETED to state.invitations.count {
                val st = it.status?.uppercase()
                st == "COMPLETED" || st == "SUBMITTED"
            },
            FeedbackFilterKey.SKIPPED to state.invitations.count { it.status.equals("SKIPPED", ignoreCase = true) }
        )
    }

    val filteredInvitations = remember(state.invitations, state.activeFilter) {
        when (state.activeFilter) {
            FeedbackFilterKey.ALL -> state.invitations
            FeedbackFilterKey.PENDING -> state.invitations.filter {
                it.status.equals("PENDING", ignoreCase = true)
            }
            FeedbackFilterKey.COMPLETED -> state.invitations.filter {
                val st = it.status?.uppercase()
                st == "COMPLETED" || st == "SUBMITTED"
            }
            FeedbackFilterKey.SKIPPED -> state.invitations.filter {
                it.status.equals("SKIPPED", ignoreCase = true)
            }
        }
    }

    PassengerPage("Đánh giá của tôi", onBack = onBack) {
        item {
            OceanBanner(
                "TRẢI NGHIỆM CHUYẾN ĐI",
                "Đánh giá của tôi",
                "Chia sẻ cảm nhận để giúp chúng tôi cải thiện chất lượng dịch vụ.",
            )
        }

        // Thông báo thành công (nếu có)
        state.message?.let { msg ->
            item {
                PassengerCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { model.clearMessages() }) {
                            Text("Đóng")
                        }
                    }
                }
            }
        }

        // Thông báo lỗi hành động (nếu có)
        if (state.error != null && state.invitations.isNotEmpty()) {
            item {
                PassengerCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { model.clearMessages() }) {
                            Text("Đóng")
                        }
                    }
                }
            }
        }

        when {
            state.loading && state.invitations.isEmpty() -> item { PassengerLoading() }
            state.error != null && state.invitations.isEmpty() -> item {
                PassengerError(state.error!!) { model.loadInvitations() }
            }
            else -> {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Lời mời đánh giá (${filteredInvitations.size}/${state.invitations.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = { model.loadInvitations() }) {
                                Text("Làm mới")
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FeedbackFilterKey.entries.forEach { filterKey ->
                                val count = counts[filterKey] ?: 0
                                FilterChip(
                                    selected = state.activeFilter == filterKey,
                                    onClick = { model.setFilter(filterKey) },
                                    label = { Text("${filterKey.label} ($count)") }
                                )
                            }
                        }
                    }
                }

                if (filteredInvitations.isEmpty()) {
                    item {
                        PassengerEmpty(
                            "Không có lời mời đánh giá",
                            if (state.activeFilter == FeedbackFilterKey.ALL) {
                                "Hiện tại bạn chưa có lời mời đánh giá nào."
                            } else {
                                "Không có lời mời nào thuộc trạng thái này."
                            }
                        )
                    }
                } else {
                    items(filteredInvitations, key = { it.id }) { invitation ->
                        val statusUpper = invitation.status?.uppercase()
                        val isPending = statusUpper == "PENDING"
                        val isCompleted = statusUpper == "COMPLETED" || statusUpper == "SUBMITTED"

                        PassengerCard {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = feedbackTypeLabel(invitation.feedbackType),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = feedbackInvitationStatusLabel(invitation.status),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }

                                Text(
                                    text = invitation.targetName?.takeIf { it.isNotBlank() } ?: "Nội dung cần đánh giá",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                invitation.bookingId?.let { bookingId ->
                                    Text(
                                        text = "Mã đặt chỗ: #$bookingId",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (isPending) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                selectedInvitation = invitation
                                                rating = 5
                                                comment = ""
                                                showSubmitDialog = true
                                            },
                                            modifier = Modifier.weight(1f),
                                            enabled = !state.sending
                                        ) {
                                            Text("Đánh giá")
                                        }

                                        OutlinedButton(
                                            onClick = { model.skipInvitation(invitation.id) },
                                            enabled = !state.sending
                                        ) {
                                            Text("Bỏ qua")
                                        }
                                    }
                                } else if (isCompleted) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Đã hoàn tất đánh giá cho mục này.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedButton(
                                            onClick = {
                                                selectedInvitation = invitation
                                                showDetailDialog = true
                                                model.loadFeedbackDetail(invitation.id)
                                            }
                                        ) {
                                            Text("Xem chi tiết")
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "Lời mời này đã được bỏ qua.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog nhập đánh giá (Rating + Comment)
    if (showSubmitDialog && selectedInvitation != null) {
        val targetInv = selectedInvitation!!
        AlertDialog(
            onDismissRequest = {
                if (!state.sending) showSubmitDialog = false
            },
            title = { Text("Đánh giá trải nghiệm") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = feedbackTypeLabel(targetInv.feedbackType),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = targetInv.targetName?.takeIf { it.isNotBlank() } ?: "Trải nghiệm của bạn",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Chọn số sao:")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (1..5).forEach { value ->
                            FilterChip(
                                selected = rating == value,
                                onClick = { rating = value },
                                label = { Text("$value ★") }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { if (it.length <= 2000) comment = it },
                        label = { Text("Nhận xét của bạn (tối đa 2000 ký tự)") },
                        supportingText = { Text("${comment.length}/2000") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        model.submitFeedback(targetInv.id, rating, comment.trim()) {
                            showSubmitDialog = false
                        }
                    },
                    enabled = !state.sending
                ) {
                    Text(if (state.sending) "Đang gửi..." else "Gửi đánh giá")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSubmitDialog = false },
                    enabled = !state.sending
                ) {
                    Text("Hủy")
                }
            }
        )
    }

    // Dialog xem chi tiết đánh giá đã gửi
    if (showDetailDialog) {
        val detail = state.selectedFeedback
        AlertDialog(
            onDismissRequest = {
                showDetailDialog = false
                model.clearSelectedFeedback()
            },
            title = { Text("Chi tiết đánh giá") },
            text = {
                when {
                    state.detailLoading -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Text("Đang tải đánh giá...")
                        }
                    }
                    detail != null -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = feedbackTypeLabel(detail.feedbackType ?: selectedInvitation?.feedbackType),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = detail.targetName ?: selectedInvitation?.targetName ?: "Nội dung đánh giá",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Đánh giá: ${detail.rating ?: 0}/5 sao",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Nhận xét:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = detail.comment?.trim()?.takeIf { it.isNotEmpty() } ?: "Không có nhận xét.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            (detail.bookingId ?: selectedInvitation?.bookingId)?.let { bookingId ->
                                Text(
                                    text = "Mã đặt chỗ: #$bookingId",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    else -> {
                        Text("Không tìm thấy nội dung đánh giá.")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDetailDialog = false
                        model.clearSelectedFeedback()
                    }
                ) {
                    Text("Đóng")
                }
            }
        )
    }
}