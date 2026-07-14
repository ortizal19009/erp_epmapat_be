package com.epmapat.erp_epmapat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.epmapat.erp_epmapat.websocket.MobileWebSocketHandler;

@Configuration
@EnableWebSocket
public class MobileWebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private MobileWebSocketHandler mobileWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mobileWebSocketHandler, "/ws/mobile")
                .setAllowedOriginPatterns("*");
    }
}
