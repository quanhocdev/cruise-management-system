
package com.project.chat.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.project.chat.model.enums.ChatDepartment;
import com.project.chat.model.enums.ConversationStatus;

public record ConversationResponse(

        UUID id,

        Long bookingId,

        String bookingCode,

        ChatDepartment department,

        ConversationStatus status,

        Instant createdAt,

        Instant updatedAt

) {
}
