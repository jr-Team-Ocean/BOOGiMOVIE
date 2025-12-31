package com.bm.project.chatting.model.websocket;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.bm.project.chatting.model.dto.ChattingMessage;
import com.bm.project.chatting.model.dto.ChattingRoom;
import com.bm.project.chatting.model.dto.Member_C;
import com.bm.project.chatting.model.service.ChattingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class ChattingWebsocketHandler extends TextWebSocketHandler{
	
	@Autowired
	private ChattingService service;

	private Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
		log.info("{}연결됨", session.getId());
	}

	public void handleMessage(WebSocketSession session, TextMessage message) throws Exception {
		log.info("전달받은 내용 : {}", message.getPayload());
		
		ObjectMapper objectMapper = new ObjectMapper();
		ChattingMessage msg = objectMapper.readValue(message.getPayload(), ChattingMessage.class);
		
		log.info("Message : {}", msg);
		
		int result = service.insertMessage(msg);
		
		if (result > 0) {
			
			ChattingRoom room = service.selectChattingRoom(msg.getChattingRoomId());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM.dd hh:mm");
			msg.setSentAt(sdf.format(new Date()));
			
			for (WebSocketSession s : sessions) {
				
				HttpSession temp = (HttpSession)s.getAttributes().get("session");
				
				int loginMemberNo = ((Member_C)temp.getAttribute("loginMember")).getMemberNo();				 
			
				if(loginMemberNo == room.getAdminMemberNo() || loginMemberNo == room.getUserMemberNo()) {
					
					s.sendMessage(new TextMessage(new Gson().toJson(msg)));
				}
			
			}
		}
	
	}

	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
	}

	
	
	
}
