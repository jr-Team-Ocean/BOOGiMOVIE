package com.bm.project.chatting.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChattingRoom {
    
    private int chattingRoomId;
    private String createdAt;    
    private int adminMemberNo;
    private int userMemberNo; 

    // 채팅 목록 조회를 위해 추가해야 하는 필드
    private int targetNo;          // 상대방 번호
    private String targetName;     // 상대방 이름
    private String targetNickName; // 상대방 닉네임
    private String targetProfile;  // 상대방 프로필
    private String lastMessage;    // 마지막 메세지
    private String sendTime;       // 보낸 시간
    private int notReadCount;      // 안읽은 메세지 수
}