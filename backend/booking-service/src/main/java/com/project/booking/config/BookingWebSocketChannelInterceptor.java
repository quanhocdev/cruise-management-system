
package com.project.booking.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Component
public class BookingWebSocketChannelInterceptor
        implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(
            BookingWebSocketChannelInterceptor.class);

    private static final String BOOKING_STATUS_DESTINATION = "/user/queue/booking-status";

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    public BookingWebSocketChannelInterceptor(
            JwtDecoder jwtDecoder,
            JwtAuthenticationConverter jwtAuthenticationConverter) {

        this.jwtDecoder = jwtDecoder;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
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

        if (StompCommand.CONNECT.equals(command)) {
            authenticate(accessor);
        }

        if (StompCommand.SUBSCRIBE.equals(command)) {
            authorizeSubscription(accessor);
        }

        if (StompCommand.SEND.equals(command)) {
            requireAuthenticatedUser(accessor);
        }

        return message;
    }

    private void authenticate(StompHeaderAccessor accessor) {

        String token = extractBearerToken(accessor);

        if (token == null) {

            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

            if (sessionAttributes != null) {

                Object cookieToken = sessionAttributes.get(
                        BookingWebSocketHandshakeInterceptor.ACCESS_TOKEN_ATTRIBUTE);

                if (cookieToken != null) {
                    token = cookieToken.toString();
                }
            }
        }

        if (token == null || token.isBlank()) {
            throw new MessageDeliveryException(
                    "WebSocket authentication token is missing.");
        }

        try {

            Jwt jwt = jwtDecoder.decode(token);

            Object userIdClaim = jwt.getClaims().get("userId");

            if (userIdClaim == null) {
                throw new IllegalArgumentException(
                        "JWT does not contain userId.");
            }

            UUID userId = UUID.fromString(userIdClaim.toString());

            var jwtAuthentication = jwtAuthenticationConverter.convert(jwt);

            if (jwtAuthentication == null) {
                throw new IllegalArgumentException(
                        "Cannot create JWT authentication.");
            }

            UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(
                    userId.toString(),
                    null,
                    jwtAuthentication.getAuthorities());

            accessor.setUser(authentication);

            log.info(
                    "[BOOKING WS] Authenticated userId={}",
                    userId);

        } catch (Exception exception) {

            log.warn(
                    "[BOOKING WS] Authentication failed: {}",
                    exception.getMessage());

            throw new MessageDeliveryException(
                    "WebSocket authentication failed.");
        }
    }

    private String extractBearerToken(
            StompHeaderAccessor accessor) {

        String authorization = accessor.getFirstNativeHeader("Authorization");

        if (authorization == null || authorization.isBlank()) {
            return null;
        }

        if (!authorization.regionMatches(
                true, 0, "Bearer ", 0, 7)) {

            throw new MessageDeliveryException(
                    "Invalid Authorization header.");
        }

        String token = authorization.substring(7).trim();

        return token.isBlank() ? null : token;
    }

    private void authorizeSubscription(
            StompHeaderAccessor accessor) {

        Principal principal = accessor.getUser();

        if (principal == null) {
            throw new MessageDeliveryException(
                    "Unauthenticated WebSocket subscription.");
        }

        String destination = accessor.getDestination();

        if (destination == null) {
            throw new MessageDeliveryException(
                    "Subscription destination is missing.");
        }

        // Giữ các topic thông báo cũ.
        if (destination.startsWith("/topic/")) {
            return;
        }

        // Chỉ cho phép user queue nhận trạng thái booking.
        if (BOOKING_STATUS_DESTINATION.equals(destination)) {
            return;
        }

        throw new MessageDeliveryException(
                "Subscription destination is not allowed.");
    }

    private void requireAuthenticatedUser(
            StompHeaderAccessor accessor) {

        if (accessor.getUser() == null) {
            throw new MessageDeliveryException(
                    "Unauthenticated WebSocket message.");
        }
    }
}
