
package com.project.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChatWebSocketAuthInterceptor authInterceptor;

    public WebSocketConfig(
            ChatWebSocketAuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // Endpoint hiện tại của React Web - giữ nguyên SockJS.
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("http://localhost:5173")
                .withSockJS();

        // Endpoint WebSocket native dành cho Android.
        registry.addEndpoint("/ws-chat-native");
    }

    @Override
    public void configureClientInboundChannel(
            ChannelRegistration registration) {
        registration.interceptors(authInterceptor);
    }
}
