
package com.project.cruise.android.data.dto.chat

data class ChatWelcomeResponse(
    val message: String,
    val sessionId: String,
    val intentName: String?,
    val suggestions: List<String>
)
