
package com.project.booking.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class BookingWebSocketHandshakeInterceptor
        implements HandshakeInterceptor {

    public static final String ACCESS_TOKEN_ATTRIBUTE = "BOOKING_WS_ACCESS_TOKEN";

    @Override
    public boolean beforeHandshake(
            org.springframework.http.server.ServerHttpRequest request,
            org.springframework.http.server.ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {

            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            Cookie[] cookies = httpRequest.getCookies();

            if (cookies != null) {

                for (Cookie cookie : cookies) {

                    if ("accessToken".equals(cookie.getName())
                            && cookie.getValue() != null
                            && !cookie.getValue().isBlank()) {

                        attributes.put(
                                ACCESS_TOKEN_ATTRIBUTE,
                                cookie.getValue());

                        break;
                    }
                }
            }
        }

        // Xác thực thực sự được thực hiện ở STOMP CONNECT.
        return true;
    }

    @Override
    public void afterHandshake(
            org.springframework.http.server.ServerHttpRequest request,
            org.springframework.http.server.ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // Không cần xử lý thêm.
    }
}
