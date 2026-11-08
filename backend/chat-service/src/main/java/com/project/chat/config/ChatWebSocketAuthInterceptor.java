
package com.project.chat.config;

import java.security.Principal;
import java.util.UUID;

import com.project.chat.service.ChatValidator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class ChatWebSocketAuthInterceptor implements ChannelInterceptor {

    private static final String CONVERSATION_TOPIC_PREFIX = "/topic/conversations/";

    private final ChatValidator chatValidator;

    public ChatWebSocketAuthInterceptor(ChatValidator chatValidator) {
        this.chatValidator = chatValidator;
    }

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message,
                StompHeaderAccessor.class);

        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();
        Principal principal = accessor.getUser();

        // 1. Kiểm tra người dùng khi kết nối STOMP
        if (command == StompCommand.CONNECT) {
            if (!(principal instanceof JwtAuthenticationToken)) {
                throw new AccessDeniedException(
                        "Unauthenticated WebSocket connection");
            }
        }

        // 2. Chỉ cho phép subscribe conversation hợp lệ
        if (command == StompCommand.SUBSCRIBE) {
            validateSubscription(accessor, principal);
        }

        // 3. Client không được tự gửi message vào topic
        // Tin nhắn phải gửi qua REST API, backend mới broadcast.
        if (command == StompCommand.SEND) {
            String destination = accessor.getDestination();

            if (destination != null
                    && destination.startsWith("/topic/")) {
                throw new AccessDeniedException(
                        "Clients cannot send directly to topics");
            }
        }

        return message;
    }

    private void validateSubscription(
            StompHeaderAccessor accessor,
            Principal principal) {
        String destination = accessor.getDestination();

        if (destination == null
                || !destination.startsWith(CONVERSATION_TOPIC_PREFIX)) {
            throw new AccessDeniedException(
                    "Invalid subscription destination");
        }

        if (!(principal instanceof JwtAuthenticationToken jwtAuth)) {
            throw new AccessDeniedException(
                    "Unauthenticated subscription");
        }

        String conversationIdValue = destination.substring(CONVERSATION_TOPIC_PREFIX.length());

        try {
            // Chỉ chấp nhận UUID đầy đủ, không chấp nhận destination dư.
            UUID conversationId = UUID.fromString(conversationIdValue);

            if (!conversationId.toString().equalsIgnoreCase(conversationIdValue)) {
                throw new IllegalArgumentException("Invalid UUID format");
            }

            // JWT sub là username, UUID nằm trong claim userId.
            String userIdValue = jwtAuth.getToken().getClaimAsString("userId");

            if (userIdValue == null || userIdValue.isBlank()) {
                throw new AccessDeniedException(
                        "Missing userId claim");
            }

            UUID userId = UUID.fromString(userIdValue);

            // Chỉ participant của conversation mới được nhận tin nhắn.
            chatValidator.validateParticipant(conversationId, userId);

        } catch (IllegalArgumentException exception) {
            throw new AccessDeniedException(
                    "Invalid conversation or user identity");
        }
    }
}
