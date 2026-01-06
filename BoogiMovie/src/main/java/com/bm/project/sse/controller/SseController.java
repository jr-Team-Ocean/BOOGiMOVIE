//package com.bm.project.sse.controller;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.SessionAttribute;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import com.bm.project.dto.MemberDto.LoginResult;
//import com.bm.project.sse.model.dto.Notification;
//import com.bm.project.sse.model.service.SseService;
//
//
//@RestController
//public class SseController {
//
//	@Autowired
//	private SseService service;
//	
//	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
//	
//	// 클라이언트 연결 요청 처리
//	@GetMapping("/sse/connect")
//	public SseEmitter sseConnect(@SessionAttribute("loginMember") LoginResult loginMember) {
//		
//		String clientId = loginMember.getMemberNo() + "";
//		
//		org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter = new SseEmitter(10 * 60 * 1000L);
//				
//		emitters.put(clientId, emitter);
//		
//		emitter.onCompletion(() -> emitters.remove(clientId));
//		
//		emitter.onTimeout(() -> emitters.remove(clientId));
//		
//		return emitter;
//	}
//	
//	// 현재 로그인한 회원이 받은 알림 중 읽지 않은 알림 개수 조회
//	@GetMapping("notification/notReadCheck")
//	public int notReadCheck(@SessionAttribute("loginMember") LoginResult loginMember) {
//		
//		long memberNo = loginMember.getMemberNo();
//		
//		return service.notReadCheck((int)memberNo);
//	}
//	
//	// 알림 삭제
//	@DeleteMapping("/notification")
//	public void deleteNotification(@RequestBody int notificationNo) {
//		service.deleteNotification(notificationNo);
//	}
//	
//	// 알림 읽음 여부 변경
//	@PutMapping("/notification")
//	public void updateNotification(@RequestBody int notificationNo) {
//		service.updateNotification(notificationNo);
//	}
//		
//	// 알림 메세지 전송 
//	
//	@PostMapping("/sse/send")
//	public void insertNotification(@RequestBody Notification notification, 
//	        @SessionAttribute(value="loginMember", required=false) LoginResult loginMember) {
//	    
//	    // 1. 세션 체크
//	    if(loginMember == null) return;
//
//	    // 2. 형변환 및 세팅
//	    notification.setSendMemberNo(loginMember.getMemberNo().intValue());
//	    
//	    // 3. 서비스 호출
//	    Map<String, Object> map = service.insertNotification(notification);
//	    
//	    // 4. 수신자 번호 가져오기 (null 체크 포함)
//	    if(map != null && map.get("receiveMemberNo") != null) {
//	        String clientId = String.valueOf(map.get("receiveMemberNo"));
//	        
//	        SseEmitter emitter = emitters.get(clientId);
//	        
//	        if(emitter != null) {
//	            try {
//	                emitter.send(map); // 알림 전송
//	            } catch(Exception e) {
//	                emitters.remove(clientId);
//	            }
//	        }
//	    }
//	}
//	
//}
