
package com.project.chat.service;

import com.project.chat.exception.AppException;
import com.project.chat.model.ChatConversation;
import com.project.chat.model.ConfirmedBooking;
import com.project.chat.model.enums.ConversationStatus;
import com.project.chat.repository.ConfirmedBookingRepository;
import com.project.chat.repository.ChatConversationRepository;
import com.project.chat.repository.ConversationParticipantRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

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

    /*
     * =====================================================
     * VALIDATE BOOKING OWNER
     * =====================================================
     */
    public ConfirmedBooking validateBookingOwner(
            Long bookingId,
            UUID userId) {
        return confirmedBookingRepository
                .findByBookingIdAndPrimaryHolderUserId(bookingId, userId)
                .orElseThrow(() -> new AppException(
                        "Không tìm thấy booking đã xác nhận hoặc bạn không có quyền truy cập",
                        HttpStatus.NOT_FOUND));
    }

    /*
     * =====================================================
     * VALIDATE CONVERSATION
     * =====================================================
     */
    public ChatConversation validateConversation(UUID conversationId) {

        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(
                        "Không tìm thấy cuộc trò chuyện",
                        HttpStatus.NOT_FOUND));
    }

    /*
     * =====================================================
     * VALIDATE PARTICIPANT
     * =====================================================
     */
    public void validateParticipant(
            UUID conversationId,
            UUID userId) {
        boolean isParticipant = participantRepository
                .existsByConversation_IdAndUserId(
                        conversationId,
                        userId);

        if (!isParticipant) {
            throw new AppException(
                    "Bạn không có quyền truy cập cuộc trò chuyện này",
                    HttpStatus.FORBIDDEN);
        }
    }

    /*
     * =====================================================
     * VALIDATE ACTIVE CONVERSATION
     * =====================================================
     */
    public void validateActiveConversation(
            ChatConversation conversation) {
        if (conversation.getStatus() != ConversationStatus.ACTIVE) {
            throw new AppException(
                    "Cuộc trò chuyện đã đóng, không thể gửi tin nhắn",
                    HttpStatus.CONFLICT);
        }
    }
}
