
package com.project.tour.controller;

import com.project.tour.dto.chat.ChatMessageRequest;
import com.project.tour.dto.chat.ChatMessageResponse;
import com.project.tour.dto.chat.ChatSessionRequest;
import com.project.tour.dto.chat.ChatWelcomeResponse;
import com.project.tour.service.chat.ChatService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/welcome")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatWelcomeResponse> openChat(
            @RequestBody(required = false) ChatSessionRequest request) {
        return ResponseEntity.ok(chatService.openChat(request));
    }

    @PostMapping("/message")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @Valid @RequestBody ChatMessageRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(request));
    }
}
