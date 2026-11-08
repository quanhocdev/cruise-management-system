
package com.project.chat.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.project.chat.model.enums.MessageStatus;
import com.project.chat.model.enums.MessageType;

public record MessageResponse(

        UUID id,

        UUID conversationId,

        UUID senderUserId,

        String content,

        MessageType messageType,

        MessageStatus messageStatus,

        Instant sentAt,

        Instant readAt

) {
}
