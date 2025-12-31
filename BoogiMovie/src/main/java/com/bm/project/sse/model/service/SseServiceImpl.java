package com.bm.project.sse.model.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.bm.project.sse.model.dao.SseMapper;
import com.bm.project.sse.model.dto.Notification;

@Service
public class SseServiceImpl implements SseService{
	
	private SseMapper mapper;

	// 알림 개수 조회
	@Override
	public int notReadCheck(int memberNo) {			
		return mapper.notReadCheck(memberNo);
	}
	
	// 메세지 삭제 
	@Override
	public void deleteNotification(int notificationNo) {		
		mapper.deleteNotification(notificationNo);		
	}

	// 알림 읽음처리 
	@Override
	public void updateNotification(int notificationNo) {
		mapper.updateNotification(notificationNo);		
	}

	// 알림 삽입
	@Override
	public Map<String, Object> insertNotification(Notification notification) {
		
		Map<String, Object> map = null;
		
		int result = mapper.insertNotification(notification);
		
		if(result > 0) {
			
			map = mapper.selectReceivedMember(notification.getNotificationNo());
			
			String url = notification.getNotificationUrl();
			
			if(url != null && url.contains("chat")) {
				
				String[] arr = url.split("chat-no=");
				
				if(arr.length> 1) {
					String chatNo = arr[arr.length - 1];
					
					map.put("notificationNo", notification.getNotificationNo());
					map.put("chattingRoomNo", chatNo);
				
				}
			}
		}
		
		return map;
	}

}
