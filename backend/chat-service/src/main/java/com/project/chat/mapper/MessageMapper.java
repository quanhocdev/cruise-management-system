
package com.project.chat.mapper;

import org.springframework.stereotype.Component;

import com.project.chat.dto.response.MessageResponse;
import com.project.chat.model.ChatMessage;

@Component
public class MessageMapper {

    public MessageResponse toResponse(ChatMessage message) {

        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                message.getSenderUserId(),
                message.getContent(),
                message.getMessageType(),
                message.getMessageStatus(),
                message.getSentAt(),
                message.getReadAt());
    }
}
