package com.project.notification.config;

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
        // Định nghĩa các prefix cho kênh (topic) mà client sẽ subscribe để nhận tin
        // realtime
        config.enableSimpleBroker("/topic", "/queue");
        // Tiền tố cho các message gửi từ client lên server (nếu có)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Điểm kết nối (Endpoint) để Frontend (React) trỏ tới khi khởi tạo Socket
        registry.addEndpoint("/ws-notification")
                .setAllowedOriginPatterns("*") // Cho phép các nguồn (origins) kết nối vào
                .withSockJS(); // Hỗ trợ fallback sang SockJS nếu trình duyệt không hỗ trợ WebSocket thuần
    }
}