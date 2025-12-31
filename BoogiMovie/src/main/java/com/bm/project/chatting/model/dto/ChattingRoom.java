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
	
}
