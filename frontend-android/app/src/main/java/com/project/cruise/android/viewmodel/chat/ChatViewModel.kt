
package com.project.cruise.android.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.chat.ChatMessageRequest
import com.project.cruise.android.data.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val suggestions: List<String> = emptyList()
)

class ChatViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _isChatOpen = MutableStateFlow(false)
    val isChatOpen: StateFlow<Boolean> = _isChatOpen.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private var hasLoadedWelcome = false

    // Giữ cùng sessionId trong suốt phiên chat của ViewModel.
    private val sessionId = UUID.randomUUID().toString()

    fun setChatOpen(isOpen: Boolean) {
        _isChatOpen.value = isOpen

        if (isOpen && !hasLoadedWelcome) {
            hasLoadedWelcome = true

            _messages.update { currentMessages ->
                if (currentMessages.isEmpty()) {
                    listOf(
                        ChatMessage(
                            text = "Dạ chào quý khách! Mình là trợ lý ảo du lịch Cruise. Tôi có thể hỗ trợ gì cho bạn hôm nay ạ?",
                            isUser = false,
                            suggestions = listOf(
                                "Giới thiệu hệ thống",
                                "Liên hệ hotline",
                                "Hướng dẫn đặt tour",
                                "Tour giá từ 10 triệu"
                            )
                        )
                    )
                } else {
                    currentMessages
                }
            }
        }
    }

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun sendMessage(text: String) {
        val message = text.trim()
        if (message.isBlank()) return

        // Tránh gửi câu hỏi rỗng; thêm câu hỏi của người dùng lên giao diện ngay.
        _messages.update {
            it + ChatMessage(
                text = message,
                isUser = true
            )
        }

        _inputText.value = ""

        viewModelScope.launch {
            try {
                val response = apiService.sendChatMessage(
                    ChatMessageRequest(
                        message = message,
                        sessionId = sessionId
                    )
                )

                _messages.update {
                    it + ChatMessage(
                        text = response.message.ifBlank {
                            "Dạ, hệ thống chưa có câu trả lời cho yêu cầu này."
                        },
                        isUser = false,
                        suggestions = listOf(
                            "Giới thiệu hệ thống",
                            "Hướng dẫn đặt tour",
                            "Liên hệ hotline"
                        )
                    )
                }
            } catch (e: Exception) {
                _messages.update {
                    it + ChatMessage(
                        text = "Dạ, hiện tại chưa thể kết nối với trợ lý du lịch. Bạn vui lòng thử lại sau nhé."
                            .plus(
                                if (e is retrofit2.HttpException) {
                                    " (HTTP ${e.code()})"
                                } else {
                                    ""
                                }
                            ),
                        isUser = false
                    )
                }
            }
        }
    }
}
