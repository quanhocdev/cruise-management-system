
package com.project.chat.mapper;

import org.springframework.stereotype.Component;

import com.project.chat.dto.response.ConversationResponse;
import com.project.chat.model.ChatConversation;

@Component
public class ConversationMapper {

    public ConversationResponse toResponse(ChatConversation conversation) {

        return new ConversationResponse(
                conversation.getId(),
                conversation.getBooking().getBookingId(),
                conversation.getBooking().getBookingCode(),
                conversation.getDepartment(),
                conversation.getStatus(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt());
    }
}
