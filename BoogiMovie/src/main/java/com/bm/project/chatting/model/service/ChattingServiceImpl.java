package com.bm.project.chatting.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bm.project.chatting.model.dao.ChattingMapper;
import com.bm.project.chatting.model.dto.ChattingRoom;
import com.bm.project.chatting.model.dto.Member;
import com.bm.project.chatting.model.dto.Message;
import com.bm.project.common.utility.Util;

@Service
public class ChattingServiceImpl implements ChattingService{

	@Autowired
	private ChattingMapper mapper;
	
	// 채팅방 목록 조회
	@Override
	public List<ChattingRoom> selectRoomList(int memberNo) {
		return mapper.selectRoomList(memberNo);
	}

	// 채팅 상대 조회
	@Override
	public List<Member> selectTarget(Map<String, Object> map) {
		return mapper.selectTarget(map);
	}

	// 채팅방 입장 (없으면 생성)
	@Override
	public int checkChattingNo(Map<String, Integer> map) {
		
		int chattingNo = mapper.checkChattingNo(map);
		
		if(chattingNo == 0) {
			chattingNo = mapper.createChattingRoom(map);
			
			if(chattingNo > 0) chattingNo = map.get("chattingNo");
		}
		return chattingNo;
	}
	
	// 채팅방 읽음 표시 (DB 업데이트) 
	@Override
	public int updateReadFlag(Map<String, Object> paramMap) {
		return mapper.updateReadFlag(paramMap);
	}

	// 채팅방 메세지 목록 조회
	@Override
	public List<Message> selectMessageList(Map<String, Object> paramMap) {
		
		List<Message> messageList = mapper.selectMessageList(Integer.parseInt(String.valueOf(paramMap.get("chattingNo"))));

		if(!messageList.isEmpty()) mapper.updateReadFlag(paramMap);
		
		return messageList;
	}

	// 메세지 삽입
	@Override
	public int insertMessage(Message msg) {
		
		msg.setMessageContent(Util.XSSHandling(msg.getMessageContent()));
		return mapper.insertMessage(msg);
	}

	
	
}
