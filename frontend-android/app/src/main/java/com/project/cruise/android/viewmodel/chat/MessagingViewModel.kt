package com.project.cruise.android.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.chat.ConfirmedBookingResponse
import com.project.cruise.android.data.dto.chat.ConversationResponse
import com.project.cruise.android.data.dto.chat.ManagerInfoResponse
import com.project.cruise.android.data.dto.chat.MessageResponse
import com.project.cruise.android.data.repository.chat.ChatRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class MessagingUiState(
    val bookings: List<ConfirmedBookingResponse> = emptyList(),
    val conversations: List<ConversationResponse> = emptyList(),
    val managers: List<ManagerInfoResponse> = emptyList(),
    val currentConversation: ConversationResponse? = null,
    val messages: List<MessageResponse> = emptyList(),

    val isLoadingBookings: Boolean = false,
    val isLoadingConversations: Boolean = false,
    val isLoadingManagers: Boolean = false,
    val isCreatingConversation: Boolean = false,
    val isLoadingMessages: Boolean = false,
    val isLoadingMoreMessages: Boolean = false,
    val isSendingMessage: Boolean = false,

    val hasMoreMessages: Boolean = false,
    val currentPage: Int = 0,
    val isSocketConnected: Boolean = false,

    val error: String? = null,
    val successMessage: String? = null
)

class MessagingViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessagingUiState())
    val uiState: StateFlow<MessagingUiState> = _uiState.asStateFlow()

    private var activeConversationId: String? = null

    // ---------------------------------------------------------
    // BOOKINGS
    // ---------------------------------------------------------

    fun loadBookings() {
        if (_uiState.value.isLoadingBookings) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoadingBookings = true, error = null)
            }

            try {
                val bookings = repository.getChatBookings()

                _uiState.update {
                    it.copy(
                        bookings = bookings,
                        isLoadingBookings = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingBookings = false,
                        error = e.toUserMessage("Không thể tải danh sách chuyến đi.")
                    )
                }
            }
        }
    }

    // ---------------------------------------------------------
    // MANAGERS
    // department phải đúng với tên enum ChatDepartment phía backend.
    // Ví dụ: OPERATION, ONBOARD, SHORE, CONVENIENCE.
    // ---------------------------------------------------------

    fun loadManagers(department: String) {
        if (department.isBlank()) {
            _uiState.update {
                it.copy(error = "Phòng ban không hợp lệ.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingManagers = true,
                    managers = emptyList(),
                    error = null
                )
            }

            try {
                val managers = repository.getChatManagers(department)

                _uiState.update {
                    it.copy(
                        managers = managers,
                        isLoadingManagers = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingManagers = false,
                        error = e.toUserMessage("Không thể tải danh sách quản lý.")
                    )
                }
            }
        }
    }

    // ---------------------------------------------------------
    // CONVERSATIONS
    // ---------------------------------------------------------

    fun loadConversations() {
        if (_uiState.value.isLoadingConversations) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingConversations = true,
                    error = null
                )
            }

            try {
                val conversations = repository.getMyConversations()

                _uiState.update {
                    it.copy(
                        conversations = conversations,
                        isLoadingConversations = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingConversations = false,
                        error = e.toUserMessage("Không thể tải danh sách hội thoại.")
                    )
                }
            }
        }
    }

    fun createOrGetConversation(
        bookingId: Long,
        department: String,
        onSuccess: (ConversationResponse) -> Unit = {}
    ) {
        if (_uiState.value.isCreatingConversation) return

        if (bookingId <= 0 || department.isBlank()) {
            _uiState.update {
                it.copy(error = "Thông tin chuyến đi hoặc phòng ban không hợp lệ.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isCreatingConversation = true,
                    error = null
                )
            }

            try {
                val conversation = repository.createOrGetConversation(
                    bookingId = bookingId,
                    department = department
                )

                _uiState.update { state ->
                    state.copy(
                        currentConversation = conversation,
                        conversations = (
                                listOf(conversation) +
                                        state.conversations.filter { it.id != conversation.id }
                                ),
                        isCreatingConversation = false
                    )
                }

                onSuccess(conversation)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCreatingConversation = false,
                        error = e.toUserMessage("Không thể tạo cuộc hội thoại.")
                    )
                }
            }
        }
    }

    // ---------------------------------------------------------
    // OPEN CONVERSATION
    // ---------------------------------------------------------

    fun openConversation(conversation: ConversationResponse) {
        if (activeConversationId != conversation.id) {
            disconnectChat()
        }

        activeConversationId = conversation.id

        _uiState.update {
            it.copy(
                currentConversation = conversation,
                messages = emptyList(),
                currentPage = 0,
                hasMoreMessages = false,
                error = null,
                isSocketConnected = false
            )
        }

        loadMessages(conversation.id)
    }

    // ---------------------------------------------------------
    // MESSAGES
    // Backend trả về Page<MessageResponse>, mặc định mới nhất trước.
    // Đảo danh sách để UI hiển thị tin nhắn cũ ở trên, mới ở dưới.
    // ---------------------------------------------------------

    fun loadMessages(
        conversationId: String,
        page: Int = 0,
        size: Int = 20
    ) {
        if (page == 0 && _uiState.value.isLoadingMessages) return
        if (page > 0 && _uiState.value.isLoadingMoreMessages) return

        viewModelScope.launch {
            _uiState.update {
                if (page == 0) {
                    it.copy(
                        isLoadingMessages = true,
                        error = null
                    )
                } else {
                    it.copy(isLoadingMoreMessages = true)
                }
            }

            try {
                val response = repository.getMessages(
                    conversationId = conversationId,
                    page = page,
                    size = size
                )

                val receivedMessages = response.content.reversed()

                _uiState.update { state ->
                    val combinedMessages = if (page == 0) {
                        receivedMessages
                    } else {
                        receivedMessages + state.messages
                    }

                    state.copy(
                        messages = combinedMessages.distinctBy { it.id },
                        currentPage = response.number,
                        hasMoreMessages = !response.last,
                        isLoadingMessages = false,
                        isLoadingMoreMessages = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingMessages = false,
                        isLoadingMoreMessages = false,
                        error = e.toUserMessage("Không thể tải tin nhắn.")
                    )
                }
            }
        }
    }

    fun loadMoreMessages() {
        val state = _uiState.value
        val conversationId = activeConversationId ?: return

        if (!state.hasMoreMessages ||
            state.isLoadingMoreMessages ||
            state.isLoadingMessages
        ) {
            return
        }

        loadMessages(
            conversationId = conversationId,
            page = state.currentPage + 1
        )
    }

    // ---------------------------------------------------------
    // SEND MESSAGE
    // Tin nhắn được lưu qua REST API.
    // WebSocket chỉ dùng để nhận tin nhắn realtime.
    // ---------------------------------------------------------

    fun sendMessage(content: String) {
        val conversationId = activeConversationId ?: run {
            _uiState.update {
                it.copy(error = "Bạn chưa mở cuộc hội thoại.")
            }
            return
        }

        val message = content.trim()

        if (message.isBlank()) return

        if (message.length > 5000) {
            _uiState.update {
                it.copy(error = "Tin nhắn không được vượt quá 5000 ký tự.")
            }
            return
        }

        if (_uiState.value.isSendingMessage) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSendingMessage = true,
                    error = null
                )
            }

            try {
                val sentMessage = repository.sendMessage(
                    conversationId = conversationId,
                    content = message
                )

                _uiState.update { state ->
                    state.copy(
                        messages = (state.messages + sentMessage)
                            .distinctBy { it.id },
                        isSendingMessage = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSendingMessage = false,
                        error = e.toUserMessage("Không thể gửi tin nhắn.")
                    )
                }
            }
        }
    }

    // ---------------------------------------------------------
    // WEBSOCKET
    // ---------------------------------------------------------

    fun connectChat(conversationId: String) {
        if (activeConversationId != conversationId) {
            activeConversationId = conversationId
        }

        if (repository.isSocketConnected() &&
            repository.getSubscribedConversationId() == conversationId
        ) {
            _uiState.update {
                it.copy(isSocketConnected = true)
            }
            return
        }

        repository.connectToConversation(
            conversationId = conversationId,
            onConnected = {
                _uiState.update {
                    it.copy(
                        isSocketConnected = true,
                        error = null
                    )
                }
            },
            onMessage = { message ->
                // Bỏ qua tin nhắn không thuộc hội thoại đang mở.
                if (message.conversationId != activeConversationId) {
                    return@connectToConversation
                }

                _uiState.update { state ->
                    state.copy(
                        messages = (state.messages + message)
                            .distinctBy { it.id }
                    )
                }
            },
            onError = { errorMessage ->
                _uiState.update {
                    it.copy(
                        isSocketConnected = false,
                        error = errorMessage
                    )
                }
            }
        )
    }

    fun disconnectChat() {
        repository.disconnectChat()

        _uiState.update {
            it.copy(isSocketConnected = false)
        }
    }

    // ---------------------------------------------------------
    // UI HELPERS
    // ---------------------------------------------------------

    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }

    fun clearSuccessMessage() {
        _uiState.update {
            it.copy(successMessage = null)
        }
    }

    fun resetCurrentConversation() {
        disconnectChat()
        activeConversationId = null

        _uiState.update {
            it.copy(
                currentConversation = null,
                messages = emptyList(),
                currentPage = 0,
                hasMoreMessages = false
            )
        }
    }

    override fun onCleared() {
        disconnectChat()
        super.onCleared()
    }

    private fun Exception.toUserMessage(defaultMessage: String): String {
        return when (this) {
            is HttpException -> {
                "Lỗi máy chủ HTTP ${code()}. Vui lòng thử lại."
            }
            else -> message ?: defaultMessage
        }
    }
}