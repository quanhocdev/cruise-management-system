package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.passenger.feedback.FeedbackInvitationResponse
import com.project.cruise.android.data.dto.passenger.feedback.FeedbackResponse
import com.project.cruise.android.data.repository.feedback.feedbackInvitationStatusLabel
import com.project.cruise.android.data.repository.feedback.feedbackTypeLabel
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.theme.OceanBanner
import com.project.cruise.android.viewmodel.passenger.FeedbackViewModel
import java.util.UUID

@Composable
fun MyFeedbackScreen(model: FeedbackViewModel, onBack: () -> Unit) {
    val state by model.state.collectAsState()

    // State phục vụ mở Dialog đánh giá cho một invitation cụ thể
    var selectedInvitation by remember { mutableStateOf<FeedbackInvitationResponse?>(null) }
    var rating by rememberSaveable { mutableStateOf(5) }
    var comment by rememberSaveable { mutableStateOf("") }
    var showSubmitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        model.loadInvitations()
    }

    PassengerPage("Đánh giá của tôi", onBack = onBack) {
        item {
            OceanBanner(
                "TRẢI NGHIỆM CHUYẾN ĐI",
                "Những điều bạn chia sẻ",
                "Xem lại lời mời đánh giá và các nhận xét của bạn.",
            )
        }

        // Hiển thị thông báo (nếu có)
        state.message?.let { msg ->
            item {
                PassengerCard {
                    Text(msg, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        when {
            state.loading -> item { PassengerLoading() }
            state.error != null -> item { PassengerError(state.error!!) { model.loadInvitations() } }
            state.invitations.isEmpty() -> item {
                PassengerEmpty("Chưa có đánh giá", "Bạn chưa có lời mời hoặc đánh giá nào.")
            }
            else -> {
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${state.invitations.size} lời mời/đánh giá", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { model.loadInvitations() }) { Text("Làm mới") }
                    }
                }

                items(state.invitations, key = { it.id }) { invitation ->
                    PassengerCard {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Loại đánh giá & Tên mục tiêu (Ví dụ: Tour, Dịch vụ...)
                            Text(
                                text = feedbackTypeLabel(invitation.feedbackType),
                                style = MaterialTheme.typography.titleMedium
                            )

                            invitation.targetName?.let {
                                Text("Đối tượng: $it", style = MaterialTheme.typography.bodyMedium)
                            }

                            Text(
                                text = "Trạng thái: ${feedbackInvitationStatusLabel(invitation.status)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            // Nếu trạng thái là PENDING thì hiện nút Gửi đánh giá hoặc Bỏ qua
                            // Nếu trạng thái là PENDING thì hiện nút Gửi đánh giá hoặc Bỏ qua
                            if (invitation.status?.toString() == "PENDING") {
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
                                        Text("Viết đánh giá")
                                    }

                                    OutlinedButton(
                                        onClick = { model.skipInvitation(invitation.id) },
                                        enabled = !state.sending
                                    ) {
                                        Text("Bỏ qua")
                                    }
                                }
                            } else {
                                // Nếu đã hoàn thành, có thể hiển thị nút xem chi tiết nếu cần
                                Text(
                                    text = "Đã hoàn tất đánh giá cho mục này.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog nhập đánh giá (Rating + Comment)
    if (showSubmitDialog && selectedInvitation != null) {
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text("Đánh giá: ${selectedInvitation?.targetName ?: "Trải nghiệm"}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        label = { Text("Chia sẻ cảm nhận (tối đa 2000 ký tự)") },
                        supportingText = { Text("${comment.length}/2000") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedInvitation?.let { inv ->
                            model.submitFeedback(inv.id, rating, comment.trim())
                        }
                        showSubmitDialog = false
                    },
                    enabled = !state.sending
                ) {
                    Text(if (state.sending) "Đang gửi..." else "Xác nhận gửi")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}