package com.bm.project.chatting.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bm.project.chatting.model.dto.ChattingRoom;
import com.bm.project.chatting.model.dto.Member_C;
import com.bm.project.chatting.model.dto.ChattingMessage;
import com.bm.project.chatting.model.service.ChattingService;
import com.bm.project.common.utility.Util;
import com.bm.project.dto.PageDto;
import com.bm.project.dto.MemberDto.LoginResult;
import com.bm.project.entity.Member;

import jakarta.servlet.http.HttpSession;

@Controller
public class ChattingController {

	@Autowired
	private ChattingService service;
	
	// 세션 관리: key=회원번호, value=SSE연결
    public static final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

		
	@GetMapping("/chatting")
	public String chatting(
			@SessionAttribute("loginMember") LoginResult loginMember,
			@RequestParam(value="cp", required=false, defaultValue="1") int cp,			
	        Model model,
	        @RequestParam Map<String, Object> paramMap // 전달 받은 파라미터들(key, query)
	        ) {
	    
	    // 1. isAdmin이 IsYN.Y 인지 확인
	    // 만약 loginMember.getIsAdmin()이 String이라면 "Y".equals()를 사용하세요.
	    boolean isAdmin = Member.IsYN.Y.equals(loginMember.getIsAdmin()) 
	    		|| "Y".equals(String.valueOf(loginMember.getIsAdmin()));
	        
	    if (isAdmin) {
	        // 관리자: 전체 채팅방 목록 조회
	    	Map<String, Object> map = service.selectRoomList(loginMember.getMemberNo(), cp);
	    	
	        model.addAttribute("map", map);
	        System.out.println("페이지" + map);
	        
	        return "admin/chatting_manager"; 
	    }
	    
	    // 2. 일반 사용자: 본인 채팅 화면으로 이동
	    return "chatting/chatting_user";
	}
	
	// 채팅 상대 검색 
	@GetMapping(value="/chatting/selectTarget", produces="application/json; charset=UTF-8")
	@ResponseBody
	public List<Member_C> selectTarget(@SessionAttribute("loginMember") LoginResult loginMember
			, @RequestParam("query") String query){
				
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("query", query);
		map.put("memberNo", loginMember.getMemberNo());
		
		return service.selectTarget(map);	
			
	}
			
	// 채팅방 입장 (해당 채팅방 번호 반환)
	@GetMapping("/chatting/enter")
	@ResponseBody
	public int chattingEnter(
	        int targetNo,
	        @SessionAttribute("loginMember") LoginResult loginMember) {
	    
	    Map<String, Object> map = new HashMap<>();
	    map.put("targetNo", targetNo);
	    
	    // ✅ "loginMemberNo"를 "memberNo"로 수정 (XML의 #{memberNo}와 일치시켜야 함)
	    map.put("memberNo", loginMember.getMemberNo()); 
	    
	    return service.checkChattingNo(map);
	}
	
	// 채팅방 목록 조회 (페이지네이션 포함)
	@GetMapping(value="/chatting/roomList", produces="application/json; charset=UTF-8")
	@ResponseBody
	public Map<String, Object> selectRoomList(
			@SessionAttribute("loginMember") LoginResult loginMember,
			@RequestParam(value="cp", defaultValue="1") int cp) {
				
		Map<String, Object> map = service.selectRoomList(loginMember.getMemberNo(), cp);
		
		return map;
		
	}
	
	// updateReadFlag
	@PutMapping("/chatting/updateReadFlag")
	@ResponseBody
	public int updateReadFlag(@RequestBody Map<String, Object> paramMap) {
	    return service.updateReadFlag(paramMap);
	}
	
	// 채팅방 메세지 목록 조회
	@GetMapping(value="/chatting/selectMessageList", produces="application/json; charset=UTF-8")
	@ResponseBody
	public List<ChattingMessage> selectMessageList(@RequestParam Map<String, Object> paramMap){
		return service.selectMessageList(paramMap);
	}
	
	
	// 1. 안읽은 개수 가져오기 (숫자 표시용)
	@GetMapping("/chatting/unreadCount")
	@ResponseBody
	public int getUnreadCount(
	        @RequestParam("chattingNo") int chattingNo, 
	        @SessionAttribute("loginMember") LoginResult loginMember) {
	    
	    Map<String, Object> map = new HashMap<>();
	    map.put("chattingNo", chattingNo);
	    map.put("memberNo", loginMember.getMemberNo());
	    
	    return service.getUnreadCount(map);
	}
	
	    @GetMapping("/user/search")
	    @ResponseBody
	    public List<ChattingMessage> search(@RequestParam Map<String, Object> map) {
	        // 검색어와 채팅방 번호를 전달받아 결과 리스트 반환
	        return service.searchChatting(map);
	    }
	    
    
	    	    
	    // 전체 안읽음 개수 조회
	    @GetMapping("/chatting/totalUnreadCount")
	    @ResponseBody
	    public int getTotalUnreadCount(@SessionAttribute("loginMember") LoginResult loginMember) {
	        // 로그인하지 않은 경우 0 반환
	        if (loginMember == null) {
	            return 0;
	        }
	        return service.getTotalUnreadCount(loginMember.getMemberNo());
	    }	        

	    // Sse 알림 연결
	    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	    public SseEmitter subscribe(@SessionAttribute("loginMember") LoginResult loginMember) {
	    	
	            SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 유지
	            Long memberNo = loginMember.getMemberNo();
	            
	            System.out.println("[SSE 연결 요청] 회원번호: " + memberNo);	            
	            
	            emitters.put(memberNo, emitter);
	            System.out.println("SSE 연결 성공" + memberNo);	            
	                        
	            emitter.onCompletion(() -> {
	            	emitters.remove(memberNo);
	            	System.out.println("SSE 연결종료 / 회원번호 :" + memberNo);
	            });      	
	            
	            
	            emitter.onTimeout(() -> {
	            	emitters.remove(memberNo);
	            	System.out.println("SSE 타임아웃/회원번호 + memberNo");
	            });
	            

	            // 연결 확인용 더미 데이터
	            try { emitter.send(SseEmitter.event().name("connect").data("0")); 
	            } catch (Exception e) {
	            	System.out.println("더미데이터 전송 불가");	            	
	            }
	            
	            return emitter;
	        }
	    
	 // 서비스에서 호출할 SSE 숫자 전송 메서드
	    public static void sendCount(Long memberNo, int totalCount) {
	    	System.out.println("SSE 전송 시도 / 회원번호 : " + memberNo + "알림개수:" + totalCount);
	        if (emitters.containsKey(memberNo)) {
	            try {
	                emitters.get(memberNo).send(totalCount);
	                System.out.println("전송 성공" );
	            } catch (Exception e) {
	            	System.out.println("전송 실패" + e.getMessage());
	                emitters.remove(memberNo);
	            }
	        }else {
	        	System.out.println("회원이 연결되어 있지 않습니다." + memberNo);
	        }
	    }
	    
	 // 채팅방 삭제
	    @DeleteMapping("/chatting/deleteRoom")
	    @ResponseBody
	    public String deleteChattingRoom(@RequestBody Map<String, Object> params) {
	        int chattingNo = Integer.parseInt(params.get("chattingNo").toString());
	        int memberNo = Integer.parseInt(params.get("memberNo").toString());
	        
	        int result = service.deleteChattingRoom(chattingNo, memberNo);
	        
	        return result > 0 ? "success" : "fail";
	    }
	    
	    
	// 관리자 번호를 가져오기 (여러 관리자용)
	    @GetMapping("/chatting/senter")
	    @ResponseBody
	    public Map<String, Object> enterAdminChat(@SessionAttribute("loginMember") LoginResult loginMember) {
	    Map<String, Object> result = new HashMap<>();
	    	try {
	        // 1. 현재 접속/활동 중인 관리자 중 순서대로 한 명을 배정하는 서비스 호출
	        // 이 안에서 관리자 리스트가 0명이면 500 에러가 날 수 있음!
	        Map<String, Object> chatInfo = service.enterAdminChat(loginMember.getMemberNo());
	        
	        // chatInfo에 chattingNo와 targetNo가 담겨있어야 함
	        result.put("chattingNo", chatInfo.get("chattingNo"));
	        result.put("targetNo", chatInfo.get("targetNo"));
	    	} catch (Exception e) {
	    		e.printStackTrace(); // 서버 콘솔에 에러 출력
	    		result.put("chattingNo", -1);
	    	}
	    	return result;
	    }

	    //이미지 업로드 (파일을 서버에 저장하고 경로만 반환)
	    @PostMapping("/chatting/uploadImage")
	    @ResponseBody
	    public String uploadImage(
	    		@RequestParam("uploadFile") MultipartFile uploadFile, 
	    		HttpSession session) throws Exception {

	    	// 1. 웹에서 접근 가능한 가상 경로
	    	String filePath = "/resources/images/chatting/";
	    	
	    	// 2. 실제 파일이 저장될 서버의 물리적 경로
	    	String realPath = session.getServletContext().getRealPath(filePath);
	    	
	    	// 3. 서비스 호출: 파일 저장 후 변경된 파일명(rename)만 받아옴
	    	// (Mapper는 건드리지 않고 파일 시스템에 저장하는 로직)
	    	String rename = service.uploadFile(uploadFile, realPath);
	    	
	    	// 4. 브라우저가 이 사진을 볼 수 있는 최종 주소 반환
	    	return filePath + rename; 
	    }
	    

}


