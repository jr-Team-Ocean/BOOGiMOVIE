package com.bm.project.chatting.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.bm.project.chatting.model.dto.ChattingRoom;
import com.bm.project.chatting.model.dto.Member_C;
import com.bm.project.chatting.model.dto.ChattingMessage;

@Mapper
public interface ChattingMapper {

	// 채팅방 목록 조회
	public List<ChattingRoom> selectRoomList(Long memberNo);
	
	// 채팅 상대 조회
	public List<Member_C> selectTarget(Map<String, Object> map);
	
	// 채팅방 입장
	public int checkChattingNo(Map<String, Object> map);
	
	// 채팅방 생성
	public int createChattingRoom(Map<String, Object> map);
	
	// 채팅 읽음 표시
	public int updateReadFlag(Map<String, Object> paramMap);
	
	// 채팅방 메세지 목록 조회
	public List<ChattingMessage> selectMessageList(int chattingNo);
	
	// 메세지 저장 
	public int insertMessage(ChattingMessage msg);

	// 채팅방 정보 가져오기
	public ChattingRoom selectChattingRoom(int chattingRoomId);


}
