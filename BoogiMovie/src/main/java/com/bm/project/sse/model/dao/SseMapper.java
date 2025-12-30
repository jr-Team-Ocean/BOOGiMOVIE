package com.bm.project.sse.model.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.bm.project.sse.model.dto.Notification;

@Mapper
public interface SseMapper {

	// 알림 개수 조회
	int notReadCheck(int memberNo);

	// 알림 삭제 
	void deleteNotification(int notificationNo);

	// 알림 읽음 처리
	void updateNotification(int notificationNo);

	// 알림 받아야 하는 회원의 번호 + 안읽은 알림 개수 조회
	Map<String, Object> selectReceivedMember(int notificationNo);

	// 알림 삽입
	int insertNotification(Notification notification);

}
