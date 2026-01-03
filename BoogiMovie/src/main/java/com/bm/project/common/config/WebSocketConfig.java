package com.bm.project.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor; // 반드시 이 패키지 확인

import com.bm.project.chatting.model.websocket.ChattingWebsocketHandler;

@Configuration
@EnableWebSocket 
public class WebSocketConfig implements WebSocketConfigurer {

	@Autowired
	private ChattingWebsocketHandler chattingWebsocketHandler;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
		registry.addHandler(chattingWebsocketHandler, "/chattingSock")
				// 기존 handshakeInterceptor 대신 스프링 내장 인터셉터를 직접 생성해서 넣으세요
				.addInterceptors(new HttpSessionHandshakeInterceptor()) 
				.setAllowedOriginPatterns("*") // 테스트를 위해 우선 모두 허용
				.withSockJS();
	}
}