
package com.project.notification.config;

import java.security.Principal;

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
public class NotificationWebSocketAuthInterceptor
        implements ChannelInterceptor {

    private static final String NOTIFICATION_DESTINATION = "/user/queue/notifications";

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

        // 1. Chỉ cho phép người dùng đã xác thực kết nối STOMP
        if (command == StompCommand.CONNECT) {
            if (!(principal instanceof JwtAuthenticationToken)) {
                throw new AccessDeniedException(
                        "Unauthenticated WebSocket connection");
            }
        }

        // 2. Chỉ cho phép subscribe vào hàng đợi thông báo cá nhân
        if (command == StompCommand.SUBSCRIBE) {
            validateSubscription(accessor, principal);
        }

        // 3. Client không được tự gửi message lên server
        // Notification chỉ được tạo và gửi bởi backend.
        if (command == StompCommand.SEND) {
            throw new AccessDeniedException(
                    "Clients cannot send messages");
        }

        return message;
    }

    private void validateSubscription(
            StompHeaderAccessor accessor,
            Principal principal) {

        String destination = accessor.getDestination();

        if (!NOTIFICATION_DESTINATION.equals(destination)) {
            throw new AccessDeniedException(
                    "Invalid notification subscription destination");
        }

        if (!(principal instanceof JwtAuthenticationToken)) {
            throw new AccessDeniedException(
                    "Unauthenticated notification subscription");
        }
    }
}
