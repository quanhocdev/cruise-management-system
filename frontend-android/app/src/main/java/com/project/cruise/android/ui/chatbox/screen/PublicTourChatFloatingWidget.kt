package com.project.cruise.android.ui.chatbox.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.chatbox.components.ChatFloatingButton
import com.project.cruise.android.ui.chatbox.components.ChatInputBar
import com.project.cruise.android.ui.chatbox.components.ChatMessageList
import com.project.cruise.android.ui.chatbox.components.ChatMessageUi
import com.project.cruise.android.ui.chatbox.components.ChatPanelHeader
import com.project.cruise.android.viewmodel.chat.ChatViewModel

/** Nút chat nổi + khung chat trợ lý du lịch cho màn Guest. */
@Composable
fun PublicTourChatFloatingWidget(chatViewModel: ChatViewModel) {
    val isOpen by chatViewModel.isChatOpen.collectAsState()
    val messages by chatViewModel.messages.collectAsState()
    val inputText by chatViewModel.inputText.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()

    val uiMessages =
        remember(messages) {
            messages.map { ChatMessageUi(it.text, it.isUser, it.suggestions) }
        }

    // Lớp nền mờ khi mở khung chat
    AnimatedVisibility(visible = isOpen, enter = fadeIn(), exit = fadeOut()) {
        Box(
            Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    chatViewModel.setChatOpen(false)
                }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp).imePadding(),
        contentAlignment = Alignment.BottomEnd,
    ) {
        if (!isOpen) {
            ChatFloatingButton(onClick = { chatViewModel.setChatOpen(true) })
        }

        AnimatedVisibility(
            visible = isOpen,
            enter =
                fadeIn() +
                        slideInVertically { it / 6 } +
                        scaleIn(initialScale = 0.92f, transformOrigin = TransformOrigin(1f, 1f)),
            exit =
                fadeOut() +
                        scaleOut(targetScale = 0.92f, transformOrigin = TransformOrigin(1f, 1f)),
        ) {
            Card(
                modifier =
                    Modifier.widthIn(max = 360.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(0.72f)
                        .heightIn(max = 560.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            ) {
                Column(Modifier.fillMaxSize()) {
                    ChatPanelHeader(onClose = { chatViewModel.setChatOpen(false) })

                    ChatMessageList(
                        messages = uiMessages,
                        isLoading = isLoading,
                        onSuggestionClick = { chatViewModel.sendMessage(it) },
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )

                    ChatInputBar(
                        value = inputText,
                        onValueChange = { chatViewModel.updateInputText(it) },
                        onSend = {
                            if (inputText.isNotBlank()) chatViewModel.sendMessage(inputText)
                        },
                        canSend = inputText.isNotBlank() && !isLoading,
                    )
                }
            }
        }
    }
}