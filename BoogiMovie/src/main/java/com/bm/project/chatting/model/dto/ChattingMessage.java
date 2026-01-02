package com.bm.project.chatting.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Getter
@Setter
@ToString

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChattingMessage {
	
	private int messageId;
	private String messageContent;
	private String sentAt;
	
	@JsonProperty("chattingNo")
	private int chattingRoomId;
	
	private String readYn;
	
	@JsonProperty("senderNo")
	private int senderId;	

}
