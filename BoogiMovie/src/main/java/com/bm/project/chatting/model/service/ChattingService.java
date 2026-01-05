package com.bm.project.chatting.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.bm.project.chatting.model.dto.ChattingRoom;
import com.bm.project.chatting.model.dto.Member_C;
import com.bm.project.chatting.model.dto.ChattingMessage;

public interface ChattingService {
	
	/** 채팅방 목록 조회
	 * @param memberNo
	 * @return roomList
	 */
	List<ChattingRoom> selectRoomList(Long memberNo);
	
	/** 채팅 상대 조회
	 * @param map
	 * @return memberList
	 */
	List<Member_C> selectTarget(Map<String, Object> map);

	/** 채팅방 입장 (없으면 생성)
	 * @param map
	 * @return chattingNo
	 */
	int checkChattingNo(Map<String, Object> map);
	
	/** 채팅 읽음 표시(업데이트)
	 * @param paramMap
	 * @return result
	 */
	int updateReadFlag(Map<String, Object> paramMap);

	/** 채팅방 메세지 목록 조회
	 * @param paramMap
	 * @return messageList
	 */
	List<ChattingMessage> selectMessageList(Map<String, Object> paramMap);
	
	/** 메세지 삽입 (글자 + 이미지 통합)
	 * @param msg (이미지가 있다면 imgPath 필드가 채워져 있어야 함)
	 * @return result
	 */
	int insertMessage(ChattingMessage msg);

	/** 채팅방 상세 정보 조회
	 * @param chattingRoomId
	 * @return room
	 */
	ChattingRoom selectChattingRoom(int chattingRoomId);

	/** 관리자 채팅방 자동 배정 및 입장
	 * @param memberNo
	 * @return chatInfo
	 */
	Map<String, Object> enterAdminChat(Long memberNo);
	
	/** 안읽은 메세지 개수 가져오기
	 * @param map
	 * @return count
	 */
	int getUnreadCount(Map<String, Object> map);
	
	/** 채팅 검색 결과 확인
	 * @param map
	 * @return searchList
	 */
	List<ChattingMessage> searchChatting(Map<String, Object> map);

	/** 이미지 파일 서버(하드디스크) 저장
	 * @param uploadFile
	 * @param realPath
	 * @return rename (변경된 파일명)
	 */
	String uploadFile(MultipartFile uploadFile, String realPath) throws Exception;

	
	/** 채팅방 삭제
	 * @param chattingNo
	 * @param memberNo
	 * @return
	 */
	int deleteChattingRoom(int chattingNo, int memberNo);
	
	/** 안읽은 채팅방 전체 개수 가져오기
	 * @param long1
	 * @return
	 */
	int getTotalUnreadCount(Long memberNo);

}
