package com.project.booking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Bật một simple memory-based message broker để chuyển tiếp tin nhắn
        // cho các client đang đăng ký các topic bắt đầu bằng "/topic"
        config.enableSimpleBroker("/topic");

        // Định nghĩa tiền tố cho các message từ client gửi lên server (nếu có)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký endpoint để Client (Web React hoặc Android) kết nối vào WebSocket
        // setAllowedOriginPatterns("*") giúp tránh lỗi CORS khi dev ở local
        registry.addEndpoint("/ws-booking")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Hỗ trợ fallback sang SockJS nếu trình duyệt cũ không hỗ trợ WebSocket thuần
    }
}