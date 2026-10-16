package com.project.cruise.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.chat.ChatViewModel

@Composable
fun PublicTourChatFloatingWidget(
    chatViewModel: ChatViewModel
) {
    val isOpen by chatViewModel.isChatOpen.collectAsState()
    val messages by chatViewModel.messages.collectAsState()
    val inputText by chatViewModel.inputText.collectAsState()

    // Lớp nền mờ khi mở khung chat
    if (isOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { chatViewModel.setChatOpen(false) }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        if (!isOpen) {
            // Nút Icon Chat nổi ở góc phải dưới
            FloatingActionButton(
                onClick = { chatViewModel.setChatOpen(true) },
                containerColor = OceanTeal,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                // Đã đổi thành ChatBubble để không bị unresolved reference
                Icon(Icons.Default.Star, contentDescription = "Mở chat", modifier = Modifier.size(28.dp))
            }
        } else {
            // Khung chat nổi (Dùng Card thay Surface để hỗ trợ shadowElevation chuẩn Material3)
            Card(
                modifier = Modifier
                    .width(360.dp)
                    .height(520.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OceanNavy)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(modifier = Modifier.size(8.dp), shape = CircleShape, color = OceanMint) {}
                            Text("Trợ Lý Du Lịch", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { chatViewModel.setChatOpen(false) }) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White)
                        }
                    }

                    // Danh sách tin nhắn cuộn lên
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages) { msg ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (msg.isUser) OceanTeal.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    Text(
                                        text = msg.text,
                                        modifier = Modifier.padding(10.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                // Các nút gợi ý (chips) nằm dưới tin nhắn của bot
                                if (!msg.isUser && msg.suggestions.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        msg.suggestions.forEach { suggestion ->
                                            OutlinedButton(
                                                onClick = {
                                                    // Tính năng điền sẵn input nếu là gợi ý có tham số
                                                    if (suggestion.contains("giá từ", ignoreCase = true)) {
                                                        chatViewModel.updateInputText("Tour giá từ 10 triệu")
                                                    } else {
                                                        chatViewModel.sendMessage(suggestion)
                                                    }
                                                },
                                                modifier = Modifier.height(32.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(suggestion, style = MaterialTheme.typography.labelSmall)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Ô nhập văn bản và nút Gửi
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { chatViewModel.updateInputText(it) },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            placeholder = { Text("Nhập tin nhắn...") },
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    chatViewModel.sendMessage(inputText)
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(OceanTeal)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Gửi", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}