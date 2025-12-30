package com.bm.project.sse.model.service;

import java.util.Map;

import com.bm.project.sse.model.dto.Notification;

public interface SseService {

	int notReadCheck(int memberNo);

	void deleteNotification(int notificationNo);

	void updateNotification(int notificationNo);

	Map<String, Object> insertNotification(Notification notification);

}
