package com.jakdang.labs.config;

import com.jakdang.labs.websocket.StompAuthInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthInterceptor stompAuthInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
        
        // STOMP 메시지 브로커 설정
        config.setPreservePublishOrder(true);
    }
    
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        // 메시지 크기 제한을 위한 설정
        return false;
    }
    
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        // WebSocket 메시지 크기 제한 설정
        registration.setMessageSizeLimit(50 * 1024 * 1024); // 50MB
        registration.setSendTimeLimit(60000); // 60초
        registration.setSendBufferSizeLimit(50 * 1024 * 1024); // 50MB
        registration.setTimeToFirstMessage(30000); // 30초
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/springboot/api/ws/notifications", "/api/springboot/ws/notifications", "/api/springboot/ws/adminchat","/springboot/api/ws/adminchat", "/ws/adminchat","/api/ws/adminchat", "/ws/notifications", "/api/ws/notifications") // 배포: /api/ws/notifications
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js")
                .setHttpMessageCacheSize(1000)
                .setDisconnectDelay(30 * 1000)
                .setHeartbeatTime(25 * 1000)
                .setStreamBytesLimit(10 * 1024 * 1024) // 10MB
                .setWebSocketEnabled(true)
                .setSessionCookieNeeded(false);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthInterceptor);
    }
}