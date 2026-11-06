
package com.project.chat.service.impl;

import com.project.chat.dto.request.SendMessageRequest;
import com.project.chat.dto.response.MessageResponse;
import com.project.chat.mapper.MessageMapper;
import com.project.chat.model.ChatConversation;
import com.project.chat.model.ChatMessage;
import com.project.chat.model.enums.MessageStatus;
import com.project.chat.model.enums.MessageType;
import com.project.chat.repository.ChatMessageRepository;
import com.project.chat.service.ChatValidator;
import com.project.chat.service.MessageService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    private final ChatMessageRepository messageRepository;
    private final ChatValidator chatValidator;
    private final MessageMapper messageMapper;

    public MessageServiceImpl(
            ChatMessageRepository messageRepository,
            ChatValidator chatValidator,
            MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.chatValidator = chatValidator;
        this.messageMapper = messageMapper;
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(
            UUID conversationId,
            SendMessageRequest request,
            UUID senderUserId) {
        ChatConversation conversation = chatValidator.validateConversation(conversationId);

        chatValidator.validateParticipant(conversationId, senderUserId);
        chatValidator.validateActiveConversation(conversation);

        ChatMessage message = new ChatMessage();

        message.setConversation(conversation);
        message.setSenderUserId(senderUserId);
        message.setContent(request.content().trim());
        message.setMessageType(MessageType.TEXT);
        message.setMessageStatus(MessageStatus.SENT);

        ChatMessage savedMessage = messageRepository.save(message);

        return messageMapper.toResponse(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessages(
            UUID conversationId,
            UUID userId,
            Pageable pageable) {
        chatValidator.validateParticipant(conversationId, userId);

        return messageRepository
                .findByConversation_IdOrderBySentAtDesc(
                        conversationId,
                        pageable)
                .map(messageMapper::toResponse);
    }
}
