
package com.project.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final NotificationWebSocketAuthInterceptor authInterceptor;

    public WebSocketConfig(
            NotificationWebSocketAuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // React Web: sử dụng SockJS
        registry.addEndpoint("/ws-notification")
                .setAllowedOriginPatterns("http://localhost:5173")
                .withSockJS();

        // Android Native: kết nối WebSocket trực tiếp
        registry.addEndpoint("/ws-notification-native");
    }

    @Override
    public void configureClientInboundChannel(
            ChannelRegistration registration) {
        registration.interceptors(authInterceptor);
    }
}
