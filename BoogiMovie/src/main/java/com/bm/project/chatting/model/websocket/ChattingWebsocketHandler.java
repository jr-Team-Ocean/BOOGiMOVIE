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
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.bm.project.chatting.model.dto.ChattingMessage;
import com.bm.project.chatting.model.dto.ChattingRoom;
import com.bm.project.chatting.model.dto.Member_C;
import com.bm.project.chatting.model.service.ChattingService;
import com.bm.project.dto.MemberDto.LoginResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChattingWebsocketHandler extends TextWebSocketHandler {
    
    @Autowired
    private ChattingService service;

    private Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("{} 연결됨", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("전달받은 내용 : {}", message.getPayload());
        
        ObjectMapper objectMapper = new ObjectMapper();
        ChattingMessage msg = objectMapper.readValue(message.getPayload(), ChattingMessage.class);
        
        // 1. DB에 채팅 메시지 삽입
        int result = service.insertMessage(msg);
        
        if (result > 0) {
            // 2. 메시지가 저장된 채팅방 정보 조회 (참가자 targetNo, adminNo 확인용)
            ChattingRoom room = service.selectChattingRoom(msg.getChattingRoomId());
            
            // 3. 전송 시간 설정
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
            msg.setSentAt(sdf.format(new Date()));
            
            // 4. 현재 접속 중인 세션들 중 해당 채팅방 참가자에게만 메시지 전달
            for (WebSocketSession s : sessions) {
                
                // WebSocket 세션에 저장된 loginMember 꺼내기
                LoginResult loginMember = (LoginResult)s.getAttributes().get("loginMember");
                
                if (loginMember != null) {
                    // LoginResult의 memberNo는 Long 타입이므로 long으로 받음
                    long loginMemberNo = loginMember.getMemberNo();
                    
                    // 채팅방 참여자(관리자 또는 유저)에게만 메시지 전송
                    if(loginMemberNo == room.getAdminMemberNo() || loginMemberNo == room.getUserMemberNo()) {
                        s.sendMessage(new TextMessage(new Gson().toJson(msg)));
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("{} 연결 종료", session.getId());
    }
}