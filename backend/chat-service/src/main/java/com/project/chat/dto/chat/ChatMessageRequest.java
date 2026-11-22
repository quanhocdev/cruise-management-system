package com.project.chat.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatMessageRequest(
        @NotBlank(message = "Nội dung tin nhắn không được để trống") @Size(max = 2000, message = "Tin nhắn không được vượt quá 2000 ký tự") String message,

        String sessionId) {
}