package com.bm.project.chatting.model.websocket;

import java.io.File; 
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.bm.project.chatting.model.dto.ChattingMessage;
import com.bm.project.chatting.model.dto.ChattingRoom;
import com.bm.project.chatting.model.service.ChattingService;
import com.bm.project.dto.MemberDto.LoginResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChattingWebsocketHandler extends TextWebSocketHandler {
    
    @Autowired
    private ChattingService service;
    
    @Autowired
    private ServletContext servletContext;

    private Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("{} 연결됨", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = objectMapper.readValue(payload, Map.class);
        
        int chattingNo = Integer.parseInt(String.valueOf(data.get("chattingNo")));
        int senderNo = Integer.parseInt(String.valueOf(data.get("senderNo")));
        String messageContent = String.valueOf(data.get("messageContent"));

        ChattingMessage msg = new ChattingMessage();
        msg.setChattingRoomId(chattingNo);
        msg.setSenderId(senderNo);
        msg.setMessageContent(messageContent);
        
        // ⭐ 이미지 처리 로직 개선
        boolean isFile = data.get("isFile") != null && String.valueOf(data.get("isFile")).equals("true");
        
        if (isFile) {
            String fileData = String.valueOf(data.get("fileData"));
            String imagePath = saveBase64Image(fileData);
            msg.setImgPath(imagePath); // DTO에 경로 주입
        } else if(data.get("imgPath") != null) {
            msg.setImgPath(String.valueOf(data.get("imgPath")));
        }

        // DB 삽입 (Service에서 이미지 테이블도 같이 인서트한다고 가정)
        int result = service.insertMessage(msg);
        
        if (result > 0) {
            ChattingRoom room = service.selectChattingRoom(chattingNo);
            if (room != null) {
                msg.setSentAt(new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date()));
                String resultJson = new Gson().toJson(msg);
                
                for (WebSocketSession s : sessions) {
                    LoginResult loginMember = (LoginResult) s.getAttributes().get("loginMember");
                    if (loginMember != null) {
                        long loginNo = Long.parseLong(String.valueOf(loginMember.getMemberNo()));
                        if (loginNo == room.getAdminMemberNo() || loginNo == room.getUserMemberNo()) {
                            s.sendMessage(new TextMessage(resultJson));
                        }
                    }
                }
            }
        }
    }

    // ⭐ Base64 이미지를 파일로 저장
    private String saveBase64Image(String base64Data) {
        try {
            String base64Image = base64Data.split(",")[1];
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);
            
            String fileName = "chat_" + System.currentTimeMillis() + ".jpg";
            String uploadDir = "/resources/images/chatting/";
            
            String realPath = servletContext.getRealPath(uploadDir);
            File folder = new File(realPath);
            if (!folder.exists()) folder.mkdirs();
            
            File file = new File(realPath + fileName);
            java.nio.file.Files.write(file.toPath(), imageBytes);
            
            return uploadDir + fileName;
        } catch (Exception e) {
            log.error("이미지 저장 실패", e);
            return null;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("{} 연결 종료", session.getId());
    }
}