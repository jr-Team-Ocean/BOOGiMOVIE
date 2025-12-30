package com.bm.project.chatting.model.service;

import java.util.List;
import java.util.Map;

import com.bm.project.chatting.model.dto.ChattingRoom;
import com.bm.project.chatting.model.dto.Member;
import com.bm.project.chatting.model.dto.Message;

public interface ChattingService {
	
	
	/*채팅방 목록 조회
	 * */
	List<ChattingRoom> selectRoomList(int memberNo);
	
	/*채팅 상대 조회
	 **/
	List<Member> selectTarget(Map<String, Object> map);

	/*채팅방 입장
	 * */
	int checkChattingNo(Map<String, Integer> map);
	
	/*채팅 읽음 표시(업데이트)
	 * */
	int updateReadFlag(Map<String, Object> paramMap);

	/*채팅방 메세지 목록 조회
	 * */
	List<Message> selectMessageList(Map<String, Object> paramMap);
	
	/*메세지 삽입
	 * */
	int insertMessage(Message msg);

}
