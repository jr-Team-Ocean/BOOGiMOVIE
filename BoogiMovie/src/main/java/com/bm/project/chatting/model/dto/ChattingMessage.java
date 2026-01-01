package com.bm.project.chatting.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChattingMessage {
	
	private int messageId;
	private String messageContent;
	private String sentAt;
	private int chattingRoomId;
	private String readYn;
	private int senderId;	

}
