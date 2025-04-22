package com.hcmute.prse_be.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint này sẽ là /ws
        // Client sẽ connect tới http(s)://your-domain/ws
        registry.addEndpoint("/wss")
                .setAllowedOriginPatterns("*")  // CORS config
                .withSockJS();  // Enable SockJS fallback
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefix cho các destination mà client có thể subscribe
        // Ví dụ: /topic/instructor/123/uploads
        config.enableSimpleBroker("/topic");

        // Prefix cho các endpoint mà client sẽ gửi message tới
        // Ví dụ: /app/send-message
        config.setApplicationDestinationPrefixes("/app");
    }
}
