package com.bm.project.sse.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
	
	private int notificationNo;
	private String notificationContent;
	private String notificationCheck;
	private String notificationDate;
	private String notificationUrl;
	private int sendMemberNo;
	private int receiveMemberNo;
	
	private String notificationType; //알림 내용 구분
		
	private String sendMemberProfileImg;
	

}
