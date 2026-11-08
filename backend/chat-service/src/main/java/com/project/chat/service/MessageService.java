
package com.project.chat.service;

import com.project.chat.dto.request.SendMessageRequest;
import com.project.chat.dto.response.MessageResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MessageService {

    MessageResponse sendMessage(
            UUID conversationId,
            SendMessageRequest request,
            UUID senderUserId);

    Page<MessageResponse> getMessages(
            UUID conversationId,
            UUID userId,
            Pageable pageable);
}
