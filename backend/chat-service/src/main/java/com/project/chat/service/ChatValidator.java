
package com.project.chat.service;

import com.project.chat.model.ChatConversation;
import com.project.chat.model.ConfirmedBooking;
import com.project.chat.repository.ConfirmedBookingRepository;
import com.project.chat.repository.ChatConversationRepository;
import com.project.chat.repository.ConversationParticipantRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class ChatValidator {

    private final ConfirmedBookingRepository confirmedBookingRepository;
    private final ChatConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;

    public ChatValidator(
            ConfirmedBookingRepository confirmedBookingRepository,
            ChatConversationRepository conversationRepository,
            ConversationParticipantRepository participantRepository) {
        this.confirmedBookingRepository = confirmedBookingRepository;
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
    }

    public ConfirmedBooking validateBookingOwner(
            Long bookingId,
            UUID userId) {
        return confirmedBookingRepository
                .findByBookingIdAndPrimaryHolderUserId(bookingId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy booking đã xác nhận hoặc bạn không có quyền truy cập"));
    }

    public ChatConversation validateConversation(UUID conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy cuộc trò chuyện"));
    }

    public void validateParticipant(
            UUID conversationId,
            UUID userId) {
        boolean isParticipant = participantRepository
                .existsByConversation_IdAndUserId(conversationId, userId);

        if (!isParticipant) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không có quyền truy cập cuộc trò chuyện này");
        }
    }

    public void validateActiveConversation(ChatConversation conversation) {
        if (conversation.getStatus() != com.project.chat.model.enums.ConversationStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cuộc trò chuyện đã đóng, không thể gửi tin nhắn");
        }
    }
}
