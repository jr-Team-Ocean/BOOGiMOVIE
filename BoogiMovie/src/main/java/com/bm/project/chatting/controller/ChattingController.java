package com.bm.project.chatting.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.bm.project.chatting.model.dto.ChattingRoom;
import com.bm.project.chatting.model.dto.Member_C;
import com.bm.project.chatting.model.dto.ChattingMessage;
import com.bm.project.chatting.model.service.ChattingService;
import com.bm.project.dto.MemberDto.LoginResult;

@Controller
public class ChattingController {

	@Autowired
	private ChattingService service;	

	
	// 채팅 페이지 전환
	@GetMapping("/chatting")
	public String chatting(@SessionAttribute("loginMember") LoginResult loginMember, 
			Model model) {
							
			// 채팅방 목록 조회
			List<ChattingRoom> roomList = service.selectRoomList(loginMember.getMemberNo());
			
			model.addAttribute("roomList", roomList);
			return "admin/chatting_manager";
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
			
	// 채팅방 입장 (없으면 생성)
	@GetMapping("/chatting/enter")
	@ResponseBody
	public int chattingEnter(int targetNo, @SessionAttribute("loginMember") LoginResult loginMember) {
		
		Map<String, Object> map = new HashMap<>();
		map.put("targetNo", targetNo);
		map.put("loginMember", loginMember.getMemberNo());
		
		return service.checkChattingNo(map);
	}
	
	// 채팅방 목록 조회
	@GetMapping(value="/chatting/roomList", produces="application/json; charset=UTF-8")
	@ResponseBody
	public List<ChattingRoom> selectRoomList(@SessionAttribute("loginMember") LoginResult loginMember){
		return service.selectRoomList(loginMember.getMemberNo());
		
	}
	
	// 채팅방 읽음 표시
	@PutMapping("/chatting/updateReadFlag")
	@ResponseBody
	public int ChattingRead(@RequestBody Map<String, Object> paramMap) {
		return service.updateReadFlag(paramMap);
	}
	
	// 채팅방 메세지 목록 조회
	@GetMapping(value="/chatting/selectMessageLIst", produces="application/json; charset=UTF-8")
	@ResponseBody
	public List<ChattingMessage> selectMessageList(@RequestParam Map<String, Object> paramMap){
		return service.selectMessageList(paramMap);
	}
	
}
