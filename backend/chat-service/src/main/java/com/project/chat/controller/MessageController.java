
package com.project.chat.controller;

import com.project.chat.dto.request.SendMessageRequest;
import com.project.chat.dto.response.MessageResponse;
import com.project.chat.security.JwtUserUtil;
import com.project.chat.service.MessageService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/message/conversations/{conversationId}/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable UUID conversationId,
            @RequestBody SendMessageRequest request,
            Principal principal) {

        UUID userId = JwtUserUtil.extractUserId(principal);

        return ResponseEntity.ok(
                messageService.sendMessage(
                        conversationId, request, userId));
    }

    @GetMapping
    public ResponseEntity<Page<MessageResponse>> getMessages(
            @PathVariable UUID conversationId,
            Principal principal,
            @PageableDefault(size = 20, sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable) {

        UUID userId = JwtUserUtil.extractUserId(principal);

        return ResponseEntity.ok(
                messageService.getMessages(
                        conversationId, userId, pageable));
    }
}
