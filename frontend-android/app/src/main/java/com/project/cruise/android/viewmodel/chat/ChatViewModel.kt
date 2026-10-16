package com.project.cruise.android.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.chat.DialogflowDetectIntentRequest
import com.project.cruise.android.data.dto.chat.EventInput
import com.project.cruise.android.data.dto.chat.QueryInput
import com.project.cruise.android.data.dto.chat.TextInput
import com.project.cruise.android.data.remote.DialogflowApiService
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
    // Inject DialogflowApiService của bạn vào đây (hoặc khởi tạo trực tiếp)
    private val dialogflowApiService: DialogflowApiService? = null
) : ViewModel() {

    private val _isChatOpen = MutableStateFlow(false)
    val isChatOpen: StateFlow<Boolean> = _isChatOpen.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private var hasLoadedWelcome = false
    private val projectId = com.project.cruise.android.BuildConfig.DIALOGFLOW_PROJECT_ID
    private val sessionId = UUID.randomUUID().toString() // Mã định danh phiên chat duy nhất cho mỗi user mở app
    private val bearerToken = "Bearer YOUR_GOOGLE_ACCESS_TOKEN" // Token lấy từ Service Account Google Cloud

    fun setChatOpen(isOpen: Boolean) {
        _isChatOpen.value = isOpen
        if (isOpen && !hasLoadedWelcome) {
            hasLoadedWelcome = true
            triggerWelcomeEvent()
        }
    }

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    private fun triggerWelcomeEvent() {
        viewModelScope.launch {
            try {
                val request = DialogflowDetectIntentRequest(
                    queryInput = QueryInput(
                        event = EventInput(name = "WELCOME", languageCode = "vi")
                    )
                )

                // Gọi thật lên Dialogflow Cloud
                val response = dialogflowApiService?.detectIntent(projectId, sessionId, bearerToken, request)
                val fulfillmentText = response?.queryResult?.fulfillmentText ?: "Dạ chào quý khách! Tôi có thể hỗ trợ gì cho bạn ạ?"

                val welcomeMsg = ChatMessage(
                    text = fulfillmentText,
                    isUser = false,
                    suggestions = listOf("Giới thiệu hệ thống", "Liên hệ hotline", "Hướng dẫn đặt tour", "Tour giá từ 10 triệu")
                )
                _messages.value = listOf(welcomeMsg)

            } catch (e: Exception) {
                // Fallback nếu mạng lỗi hoặc chưa cấu hình token
                val fallbackMsg = ChatMessage(
                    text = "Dạ chào quý khách! Mình là trợ lý ảo du lịch Cruise. Tôi có thể hỗ trợ gì cho bạn hôm nay ạ?",
                    isUser = false,
                    suggestions = listOf("Giới thiệu hệ thống", "Liên hệ hotline", "Hướng dẫn đặt tour", "Tour giá từ 10 triệu")
                )
                _messages.value = listOf(fallbackMsg)
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMsg = ChatMessage(text = text, isUser = true)
        _messages.update { it + userMsg }
        _inputText.value = ""

        viewModelScope.launch {
            try {
                val request = DialogflowDetectIntentRequest(
                    queryInput = QueryInput(
                        text = TextInput(text = text, languageCode = "vi")
                    )
                )

                // Gửi text thực tế lên Dialogflow Cloud
                val response = dialogflowApiService?.detectIntent(projectId, sessionId, bearerToken, request)
                val botReplyText = response?.queryResult?.fulfillmentText ?: "Dạ hệ thống đã ghi nhận yêu cầu của bạn."

                val botReply = ChatMessage(
                    text = botReplyText,
                    isUser = false,
                    suggestions = listOf("Giới thiệu hệ thống", "Hướng dẫn đặt tour", "Liên hệ hotline")
                )
                _messages.update { it + botReply }

            } catch (e: Exception) {
                val errorReply = ChatMessage(
                    text = "Dạ hệ thống đang gặp gián đoạn kết nối, bạn thử lại sau nhé!",
                    isUser = false
                )
                _messages.update { it + errorReply }
            }
        }
    }
}