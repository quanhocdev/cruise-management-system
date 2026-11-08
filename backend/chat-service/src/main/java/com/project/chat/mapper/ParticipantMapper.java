
package com.project.chat.mapper;

import org.springframework.stereotype.Component;

import com.project.chat.dto.response.ParticipantResponse;
import com.project.chat.model.ConversationParticipant;

@Component
public class ParticipantMapper {

    public ParticipantResponse toResponse(
            ConversationParticipant participant) {

        return new ParticipantResponse(
                participant.getId(),
                participant.getUserId(),
                participant.getParticipantRole(),
                participant.getJoinedAt(),
                participant.getLastReadAt());
    }
}
