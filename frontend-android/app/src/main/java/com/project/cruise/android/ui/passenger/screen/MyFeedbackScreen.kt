package com.project.cruise.android.ui.passenger.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.project.cruise.android.data.dto.passenger.feedback.FeedbackInvitationResponse
import com.project.cruise.android.data.repository.feedback.feedbackTypeLabel
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.passenger.components.feedback.*
import com.project.cruise.android.ui.theme.OceanGoldDeep
import com.project.cruise.android.ui.theme.OceanNavy
import com.project.cruise.android.viewmodel.passenger.FeedbackFilterKey
import com.project.cruise.android.viewmodel.passenger.FeedbackViewModel

@Composable
fun MyFeedbackScreen(model: FeedbackViewModel, onBack: () -> Unit) {
    val state by model.state.collectAsState()

    // Lời mời đang được đánh giá / xem chi tiết
    var selectedInvitation by remember { mutableStateOf<FeedbackInvitationResponse?>(null) }
    var rating by rememberSaveable { mutableStateOf(5) }
    var comment by rememberSaveable { mutableStateOf("") }
    var showSubmitDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { model.loadInvitations() }

    val counts = remember(state.invitations) {
        mapOf(
            FeedbackFilterKey.ALL to state.invitations.size,
            FeedbackFilterKey.PENDING to state.invitations.count { feedbackStatusOf(it.status) == FeedbackStatus.PENDING },
            FeedbackFilterKey.COMPLETED to state.invitations.count { feedbackStatusOf(it.status) == FeedbackStatus.COMPLETED },
            FeedbackFilterKey.SKIPPED to state.invitations.count { feedbackStatusOf(it.status) == FeedbackStatus.SKIPPED },
        )
    }
    val pendingCount = counts[FeedbackFilterKey.PENDING] ?: 0
    val completedCount = counts[FeedbackFilterKey.COMPLETED] ?: 0

    val filtered = remember(state.invitations, state.activeFilter) {
        when (state.activeFilter) {
            FeedbackFilterKey.ALL -> state.invitations
            FeedbackFilterKey.PENDING -> state.invitations.filter { feedbackStatusOf(it.status) == FeedbackStatus.PENDING }
            FeedbackFilterKey.COMPLETED -> state.invitations.filter { feedbackStatusOf(it.status) == FeedbackStatus.COMPLETED }
            FeedbackFilterKey.SKIPPED -> state.invitations.filter { feedbackStatusOf(it.status) == FeedbackStatus.SKIPPED }
        }
    }
    val error = state.error

    PassengerPage(
        title = "Đánh giá của tôi",
        subtitle = if (pendingCount > 0) "$pendingCount lời mời đang chờ bạn" else "Cảm ơn bạn đã chia sẻ",
        onBack = onBack,
    ) {
        state.message?.let { msg ->
            item(key = "msg") { FeedbackMessageBanner(msg, isError = false, onDismiss = { model.clearMessages() }) }
        }
        if (error != null && state.invitations.isNotEmpty()) {
            item(key = "action-error") { FeedbackMessageBanner(error, isError = true, onDismiss = { model.clearMessages() }) }
        }

        when {
            state.loading && state.invitations.isEmpty() -> item(key = "loading") { PassengerLoading() }
            error != null && state.invitations.isEmpty() -> item(key = "error") {
                PassengerError(error) { model.loadInvitations() }
            }
            state.invitations.isEmpty() -> item(key = "empty") {
                PassengerEmpty("Chưa có lời mời đánh giá", "Lời mời sẽ xuất hiện sau khi chuyến đi của bạn kết thúc.")
            }
            else -> {
                item(key = "hero") {
                    FeedbackHeroCard(pending = pendingCount, completed = completedCount, total = state.invitations.size)
                }
                item(key = "filters") {
                    FeedbackFilterChips(counts, state.activeFilter, onSelect = { model.setFilter(it) })
                }
                item(key = "list-header") {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Lời mời đánh giá (${filtered.size}/${state.invitations.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OceanNavy,
                        )
                        TextButton(onClick = { model.loadInvitations() }) {
                            Text("Làm mới", color = OceanGoldDeep, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (filtered.isEmpty()) {
                    item(key = "filtered-empty") {
                        PassengerEmpty(
                            "Không có lời mời đánh giá",
                            if (state.activeFilter == FeedbackFilterKey.ALL) "Hiện tại bạn chưa có lời mời đánh giá nào."
                            else "Không có lời mời nào thuộc trạng thái này.",
                        )
                    }
                } else {
                    items(filtered, key = { it.id }) { invitation ->
                        FeedbackInvitationCard(
                            invitation = invitation,
                            sending = state.sending,
                            onRate = { stars ->
                                selectedInvitation = invitation
                                rating = stars
                                comment = ""
                                showSubmitDialog = true
                            },
                            onSkip = { model.skipInvitation(invitation.id) },
                            onViewDetail = {
                                selectedInvitation = invitation
                                showDetailDialog = true
                                model.loadFeedbackDetail(invitation.id)
                            },
                        )
                    }
                }
            }
        }
    }

    val target = selectedInvitation
    if (showSubmitDialog && target != null) {
        FeedbackSubmitDialog(
            typeLabel = feedbackTypeLabel(target.feedbackType),
            targetName = target.targetName?.takeIf { it.isNotBlank() } ?: "Trải nghiệm của bạn",
            rating = rating,
            onRatingChange = { rating = it },
            comment = comment,
            onCommentChange = { comment = it },
            sending = state.sending,
            onSubmit = { model.submitFeedback(target.id, rating, comment.trim()) { showSubmitDialog = false } },
            onDismiss = { showSubmitDialog = false },
        )
    }

    if (showDetailDialog) {
        val detail = state.selectedFeedback
        FeedbackDetailDialog(
            loading = state.detailLoading,
            found = detail != null,
            typeLabel = feedbackTypeLabel(detail?.feedbackType ?: selectedInvitation?.feedbackType),
            targetName = detail?.targetName ?: selectedInvitation?.targetName ?: "Nội dung đánh giá",
            rating = (detail?.rating as? Number)?.toInt() ?: 0,
            comment = detail?.comment,
            bookingId = detail?.bookingId ?: selectedInvitation?.bookingId,
            onDismiss = {
                showDetailDialog = false
                model.clearSelectedFeedback()
            },
        )
    }
}