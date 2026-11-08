
package com.project.cruise.android.data.network

import com.google.gson.Gson
import com.project.cruise.android.data.dto.chat.MessageResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.UUID
import java.util.concurrent.TimeUnit

class ChatSocketService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()
) {

    private val gson = Gson()

    @Volatile
    private var webSocket: WebSocket? = null

    @Volatile
    private var subscribedConversationId: String? = null

    @Volatile
    private var connected = false

    private val frameBuffer = StringBuilder()

    /**
     * Kết nối WebSocket và đăng ký nhận tin nhắn
     * của một conversation.
     */
    fun connect(
        baseUrl: String,
        accessToken: String,
        conversationId: String,
        onConnected: () -> Unit,
        onMessage: (MessageResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        if (accessToken.isBlank()) {
            onError("Access token không hợp lệ")
            return
        }

        if (runCatching { UUID.fromString(conversationId) }.isFailure) {
            onError("Conversation ID không hợp lệ")
            return
        }

        disconnect()

        val wsUrl = baseUrl
            .trimEnd('/')
            .replaceFirst("https://", "wss://")
            .replaceFirst("http://", "ws://") + "/ws-chat-native"

        val request = Request.Builder()
            .url(wsUrl)
            .header("Authorization", "Bearer $accessToken")
            .build()

        val listener = object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                this@ChatSocketService.webSocket = webSocket

                val connectFrame = buildString {
                    append("CONNECT\n")
                    append("accept-version:1.2\n")
                    append("host:localhost\n")
                    append("heart-beat:0,0\n")
                    append("\n")
                    append('\u0000')
                }

                webSocket.send(connectFrame)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                synchronized(frameBuffer) {
                    frameBuffer.append(text)

                    while (true) {
                        val frameEnd = frameBuffer.indexOf("\u0000")

                        if (frameEnd < 0) break

                        val frame = frameBuffer
                            .substring(0, frameEnd)
                            .trim()

                        frameBuffer.delete(0, frameEnd + 1)

                        if (frame.isBlank()) continue

                        when {
                            frame.startsWith("CONNECTED") -> {
                                connected = true
                                subscribedConversationId = conversationId

                                val subscribeFrame = buildString {
                                    append("SUBSCRIBE\n")
                                    append("id:sub-$conversationId\n")
                                    append("destination:/topic/conversations/$conversationId\n")
                                    append("ack:auto\n")
                                    append("\n")
                                    append('\u0000')
                                }

                                webSocket.send(subscribeFrame)
                                onConnected()
                            }

                            frame.startsWith("MESSAGE") -> {
                                val bodySeparator = frame.indexOf("\n\n")

                                if (bodySeparator < 0) continue

                                val body = frame
                                    .substring(bodySeparator + 2)
                                    .trim()

                                try {
                                    val message = gson.fromJson(
                                        body,
                                        MessageResponse::class.java
                                    )

                                    if (message != null) {
                                        onMessage(message)
                                    }
                                } catch (exception: Exception) {
                                    onError(
                                        "Không thể đọc tin nhắn: ${exception.message}"
                                    )
                                }
                            }

                            frame.startsWith("ERROR") -> {
                                onError("STOMP server trả về lỗi: $frame")
                            }
                        }
                    }
                }
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: Response?
            ) {
                connected = false
                subscribedConversationId = null

                onError(
                    response?.let {
                        "WebSocket lỗi HTTP ${it.code}: ${t.message}"
                    } ?: "Không thể kết nối WebSocket: ${t.message}"
                )
            }

            override fun onClosed(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                connected = false
                subscribedConversationId = null
            }
        }

        webSocket = client.newWebSocket(request, listener)
    }

    /**
     * Ngắt kết nối WebSocket.
     */
    fun disconnect() {
        val socket = webSocket

        if (socket != null && connected) {
            val disconnectFrame = "DISCONNECT\n\n\u0000"
            socket.send(disconnectFrame)
        }

        socket?.close(1000, "Chat disconnected")

        webSocket = null
        connected = false
        subscribedConversationId = null

        synchronized(frameBuffer) {
            frameBuffer.clear()
        }
    }

    fun isConnected(): Boolean = connected

    fun getSubscribedConversationId(): String? =
        subscribedConversationId
}
