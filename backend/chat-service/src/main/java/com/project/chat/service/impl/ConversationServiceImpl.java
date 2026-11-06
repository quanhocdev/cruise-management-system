
package com.project.chat.service.impl;

import com.project.chat.dto.request.CreateConversationRequest;
import com.project.chat.dto.response.ConversationResponse;
import com.project.chat.dto.response.ParticipantResponse;
import com.project.chat.mapper.ConversationMapper;
import com.project.chat.mapper.ParticipantMapper;
import com.project.chat.model.ChatConversation;
import com.project.chat.model.ConfirmedBooking;
import com.project.chat.model.ConversationParticipant;
import com.project.chat.model.enums.ChatDepartment;
import com.project.chat.model.enums.ConversationStatus;
import com.project.chat.model.enums.ParticipantRole;
import com.project.chat.repository.ChatConversationRepository;
import com.project.chat.repository.ConversationParticipantRepository;
import com.project.chat.service.ChatValidator;
import com.project.chat.service.ConversationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final ChatConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;

    private final ChatValidator chatValidator;
    private final ConversationMapper conversationMapper;
    private final ParticipantMapper participantMapper;

    public ConversationServiceImpl(
            ChatConversationRepository conversationRepository,
            ConversationParticipantRepository participantRepository,
            ChatValidator chatValidator,
            ConversationMapper conversationMapper,
            ParticipantMapper participantMapper) {
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.chatValidator = chatValidator;
        this.conversationMapper = conversationMapper;
        this.participantMapper = participantMapper;
    }

    @Override
    @Transactional
    public ConversationResponse createOrGetConversation(
            CreateConversationRequest request,
            UUID userId) {
        ConfirmedBooking booking = chatValidator.validateBookingOwner(
                request.bookingId(),
                userId);

        ChatDepartment department = request.department();

        // Nếu hội thoại đã tồn tại thì mở lại hội thoại đó.
        ChatConversation existingConversation = conversationRepository
                .findByBooking_BookingIdAndDepartment(
                        booking.getBookingId(),
                        department)
                .orElse(null);

        if (existingConversation != null) {

            // Mở lại hội thoại nếu trước đó đã đóng.
            if (existingConversation.getStatus() == ConversationStatus.CLOSED) {
                existingConversation.setStatus(ConversationStatus.ACTIVE);
                existingConversation.setClosedAt(null);

                existingConversation = conversationRepository.save(existingConversation);
            }

            addPassengerIfNotExists(existingConversation, userId);

            return conversationMapper.toResponse(existingConversation);
        }

        ChatConversation conversation = new ChatConversation();

        conversation.setBooking(booking);
        conversation.setDepartment(department);
        conversation.setStatus(ConversationStatus.ACTIVE);

        ChatConversation savedConversation = conversationRepository.save(conversation);

        // Thêm Passenger tạo hội thoại vào participant.
        addPassengerIfNotExists(savedConversation, userId);

        return conversationMapper.toResponse(savedConversation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getMyConversations(UUID userId) {

        return participantRepository.findByUserId(userId)
                .stream()
                .map(ConversationParticipant::getConversation)
                .map(conversationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationResponse getConversationDetail(
            UUID conversationId,
            UUID userId) {
        chatValidator.validateParticipant(conversationId, userId);

        ChatConversation conversation = chatValidator.validateConversation(conversationId);

        return conversationMapper.toResponse(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipantResponse> getParticipants(
            UUID conversationId,
            UUID userId) {
        chatValidator.validateParticipant(conversationId, userId);

        return participantRepository.findByConversation_Id(conversationId)
                .stream()
                .map(participantMapper::toResponse)
                .toList();
    }

    private void addPassengerIfNotExists(
            ChatConversation conversation,
            UUID userId) {
        boolean exists = participantRepository
                .existsByConversation_IdAndUserId(
                        conversation.getId(),
                        userId);

        if (exists) {
            return;
        }

        ConversationParticipant participant = new ConversationParticipant();

        participant.setConversation(conversation);
        participant.setUserId(userId);
        participant.setParticipantRole(ParticipantRole.PASSENGER);

        participantRepository.save(participant);
    }
}
