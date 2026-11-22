package com.project.chat.dto.chat;

public record ChatMessageResponse(
        String message,
        String sessionId,
        String intentName) {
}