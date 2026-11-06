
package com.project.chat.controller;

import com.project.chat.dto.request.CreateConversationRequest;
import com.project.chat.dto.response.ConversationResponse;
import com.project.chat.dto.response.ParticipantResponse;
import com.project.chat.security.JwtUserUtil;
import com.project.chat.service.ConversationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> createOrGetConversation(
            @RequestBody CreateConversationRequest request,
            Principal principal) {

        UUID userId = JwtUserUtil.extractUserId(principal);

        return ResponseEntity.ok(
                conversationService.createOrGetConversation(request, userId));
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getMyConversations(
            Principal principal) {

        UUID userId = JwtUserUtil.extractUserId(principal);

        return ResponseEntity.ok(
                conversationService.getMyConversations(userId));
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationResponse> getConversationDetail(
            @PathVariable UUID conversationId,
            Principal principal) {

        UUID userId = JwtUserUtil.extractUserId(principal);

        return ResponseEntity.ok(
                conversationService.getConversationDetail(
                        conversationId, userId));
    }

    @GetMapping("/{conversationId}/participants")
    public ResponseEntity<List<ParticipantResponse>> getParticipants(
            @PathVariable UUID conversationId,
            Principal principal) {

        UUID userId = JwtUserUtil.extractUserId(principal);

        return ResponseEntity.ok(
                conversationService.getParticipants(
                        conversationId, userId));
    }
}
