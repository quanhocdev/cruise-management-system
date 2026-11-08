
package com.project.chat.service;

import com.project.chat.dto.request.CreateConversationRequest;
import com.project.chat.dto.response.ConversationResponse;
import com.project.chat.dto.response.ParticipantResponse;

import java.util.List;
import java.util.UUID;

public interface ConversationService {

    ConversationResponse createOrGetConversation(
            CreateConversationRequest request,
            UUID userId);

    List<ConversationResponse> getMyConversations(UUID userId);

    ConversationResponse getConversationDetail(
            UUID conversationId,
            UUID userId);

    List<ParticipantResponse> getParticipants(
            UUID conversationId,
            UUID userId);
}
