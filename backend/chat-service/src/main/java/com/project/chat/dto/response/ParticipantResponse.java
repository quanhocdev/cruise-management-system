
package com.project.chat.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.project.chat.model.enums.ParticipantRole;

public record ParticipantResponse(

        UUID id,

        UUID userId,

        ParticipantRole participantRole,

        Instant joinedAt,

        Instant lastReadAt

) {
}
