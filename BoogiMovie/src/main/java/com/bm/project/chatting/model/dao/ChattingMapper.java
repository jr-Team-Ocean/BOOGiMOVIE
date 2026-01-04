package com.bm.project.chatting.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.bm.project.chatting.model.dto.ChattingRoom;
import com.bm.project.chatting.model.dto.Member_C;
import com.bm.project.chatting.model.dto.ChattingImage;
import com.bm.project.chatting.model.dto.ChattingMessage;

@Mapper
public interface ChattingMapper {

    // 1. 채팅방 관련
    List<ChattingRoom> selectRoomList(Long memberNo);        // 채팅방 목록 조회
    int checkChattingNo(Map<String, Object> map);           // 채팅방 번호 확인
    int createChattingRoom(Map<String, Object> map);        // 채팅방 생성
    ChattingRoom selectChattingRoom(int chattingRoomId);    // 채팅방 상세 정보 조회
    List<Integer> enterAdminChat();                         // 관리자 번호 목록 조회

    // 2. 메시지 관련
    List<ChattingMessage> selectMessageList(Map<String, Object> map); // 메시지 내역 조회
    int insertMessage(ChattingMessage msg);                           // 일반 메시지 삽입
    int updateReadFlag(Map<String, Object> paramMap);                // 읽음 표시 업데이트
    int getUnreadCount(Map<String, Object> map);                      // 안읽은 메시지 수 조회
    List<ChattingMessage> searchChatting(Map<String, Object> map);     // 메시지 검색

    // 3. 이미지 관련
    /** * 이미지 정보 삽입
     * @param img (messageId, chattingImagePath 포함)
     * @return result
     */
    int insertChattingImg(ChattingImage img);

    // 4. 기타
    List<Member_C> selectTarget(Map<String, Object> map);   // 채팅 상대 검색
    
    // 채팅방 삭제
	int deleteChattingRoom(int chattingNo, int memberNo);
	
	// 전체 안읽음 개수 조회
	int getTotalUnreadCount(int memberNo);
	
}