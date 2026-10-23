package com.project.tour.dto.chat;

public record ChatMessageResponse(
        String message,
        String sessionId,
        String intentName) {
}