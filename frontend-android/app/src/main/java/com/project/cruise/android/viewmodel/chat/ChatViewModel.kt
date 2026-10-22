
package com.project.cruise.android.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.chat.ChatMessageRequest
import com.project.cruise.android.data.dto.chat.ChatSessionRequest
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var hasLoadedWelcome = false
    private var isLoadingWelcome = false

    private var sessionId = UUID.randomUUID().toString()

    fun setChatOpen(isOpen: Boolean) {
        _isChatOpen.value = isOpen

        if (isOpen && !hasLoadedWelcome && !isLoadingWelcome) {
            loadWelcome()
        }
    }

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    private fun loadWelcome() {
        viewModelScope.launch {
            isLoadingWelcome = true
            _isLoading.value = true

            try {
                val response = apiService.openChat(
                    ChatSessionRequest(sessionId = sessionId)
                )

                sessionId = response.sessionId

                _messages.update { current ->
                    current + ChatMessage(
                        text = response.message,
                        isUser = false,
                        suggestions = response.suggestions
                    )
                }

                // Chỉ đánh dấu thành công sau khi backend trả lời.
                hasLoadedWelcome = true

            } catch (e: Exception) {
                _messages.update {
                    it + ChatMessage(
                        text = "Dạ, hiện tại chưa thể kết nối với trợ lý du lịch. "
                                + "Bạn vui lòng đóng khung chat rồi mở lại để thử lại nhé.",
                        isUser = false
                    )
                }
            } finally {
                isLoadingWelcome = false
                _isLoading.value = false
            }
        }
    }

    fun sendMessage(text: String) {
        val message = text.trim()

        if (message.isBlank() || _isLoading.value) return

        _isLoading.value = true

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

                sessionId = response.sessionId

                _messages.update {
                    it + ChatMessage(
                        text = response.message,
                        isUser = false
                    )
                }
            } catch (e: Exception) {
                val errorText = if (e is retrofit2.HttpException) {
                    "Dạ, hệ thống phản hồi lỗi HTTP ${e.code()}. Bạn vui lòng thử lại nhé."
                } else {
                    "Dạ, hiện tại chưa thể kết nối với trợ lý du lịch. Bạn vui lòng thử lại nhé."
                }

                _messages.update {
                    it + ChatMessage(
                        text = errorText,
                        isUser = false
                    )
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}
