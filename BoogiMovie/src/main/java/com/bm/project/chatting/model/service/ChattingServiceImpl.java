package com.bm.project.chatting.model.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bm.project.chatting.controller.ChattingController;
import com.bm.project.chatting.model.dao.ChattingMapper;
import com.bm.project.chatting.model.dto.ChattingRoom;
import com.bm.project.chatting.model.dto.Member_C;
import com.bm.project.chatting.model.dto.ChattingMessage;
import com.bm.project.chatting.model.dto.ChattingImage;
import com.bm.project.common.utility.Util;
import com.bm.project.dto.PageDto;

import org.springframework.transaction.annotation.Transactional;


@Service
public class ChattingServiceImpl implements ChattingService {

    @Autowired
    private ChattingMapper mapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertMessage(ChattingMessage msg) {
	    // 1. 메시지 저장 (기존 로직)
	    int result = mapper.insertMessage(msg);
	
	    // 2. 이미지 저장 (기존 로직)
	    if (result > 0 && msg.getImgPath() != null) {
	        ChattingImage img = new ChattingImage();
	        img.setMessageId(String.valueOf(msg.getMessageId()));
	        img.setChattingImagePath(msg.getImgPath());
	        mapper.insertChattingImg(img);      

	    }
	
	    // 3. 실시간 알림 연동
	    if (result > 0) {
	        // 1. Map 생성 및 데이터 담기 (DTO 변수명에 맞춤)
	        Map<String, Object> params = new HashMap<>();
	        params.put("chattingNo", msg.getChattingRoomId()); 
	        params.put("senderNo", msg.getSenderId());        

	        // 2. 수신자 ID 조회
	        int receiverId = mapper.getReceiverId(params);
	        
	        // 3. 알림 전송
	        int totalCount = mapper.getTotalUnreadCount((long)receiverId);
	        
	        System.out.println("메세지 저장 성공 / 상대방" + receiverId);
	        
	        ChattingController.sendCount((long)receiverId, totalCount);
	    }

	    return result;
    }


    // 2. 이미지 파일 서버 실제 저장
    // Controller에서 호출하여 파일을 하드디스크에 저장하고 이름을 반환함
    @Override
    public String uploadFile(MultipartFile uploadFile, String realPath) throws Exception {
        File folder = new File(realPath);
        if(!folder.exists()) folder.mkdirs();

        String rename = Util.fileRename(uploadFile.getOriginalFilename());
        uploadFile.transferTo(new File(realPath + rename));

        return rename;
    }

    @Override
    public PageDto<ChattingRoom> selectRoomList(Long memberNo, int cp) {
        
    	int limit = 10; // 페이지당 채팅방 개수
    	
    	
    	int totalCount = mapper.getListCount(memberNo); // 전체 채팅방 수 조회
    	
    	int start = (cp - 1) * limit + 1; // 처음 번호
    	int end = cp * limit; // 끝 번호
    	
    	// 전달 객체 생성
    	Map<String, Object> map = new HashMap<>();
    	map.put("memberNo", memberNo);
    	map.put("start", start);
    	map.put("end", end);
    	
    	// 페이지 포함 목록 조회(매퍼에 메서드 추가 필요) 
    	List<ChattingRoom> roomList = mapper.selectRoomListPaging(map);
    	
    	return new PageDto<>(roomList, cp, totalCount, limit);
    }

    @Override
    public List<Member_C> selectTarget(Map<String, Object> map) {
        return mapper.selectTarget(map);
    }

    @Override
    public int checkChattingNo(Map<String, Object> map) {
        int chattingNo = mapper.checkChattingNo(map);
        if(chattingNo == 0) {
            chattingNo = mapper.createChattingRoom(map);
            if(chattingNo > 0) chattingNo = (int)map.get("chattingNo");
        }
        return chattingNo;
    }

    @Override
    public int updateReadFlag(Map<String, Object> paramMap) {
        int result = mapper.updateReadFlag(paramMap);        
        
        if(result > 0) {
            int myNo = Integer.parseInt(paramMap.get("memberNo").toString());
            int totalCount = mapper.getTotalUnreadCount((long)myNo);
            ChattingController.sendCount((long)myNo, totalCount);
        }
        return result;
    }

    @Override
    public List<ChattingMessage> selectMessageList(Map<String, Object> map) {
        Object chattingNo = map.get("chattingNo");
        if(chattingNo == null || String.valueOf(chattingNo).equals("undefined")) {
            return new ArrayList<>();
        }
        return mapper.selectMessageList(map);
    }

    @Override
    public ChattingRoom selectChattingRoom(int chattingRoomId) {
        return mapper.selectChattingRoom(chattingRoomId);
    }

    @Override
    public Map<String, Object> enterAdminChat(Long memberNo) {
        Map<String, Object> map = new HashMap<>();
        map.put("memberNo", memberNo);
        List<Integer> adminList = mapper.enterAdminChat();
        if (adminList.isEmpty()) throw new RuntimeException("채팅 가능한 관리자가 없습니다.");
        
        int targetNo = adminList.get(0); 
        map.put("targetNo", targetNo);
        int chattingNo = mapper.checkChattingNo(map);

        if (chattingNo == 0) { 
            mapper.createChattingRoom(map); 
            chattingNo = (int)map.get("chattingNo");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("chattingNo", chattingNo);
        result.put("targetNo", targetNo);
        return result;
    }

    @Override
    public int getUnreadCount(Map<String, Object> map) {
        return mapper.getUnreadCount(map);
    }

    @Override
    public List<ChattingMessage> searchChatting(Map<String, Object> map) {
        return mapper.searchChatting(map);
    }

    // 채팅방 삭제
	@Override
	public int deleteChattingRoom(int chattingNo, int memberNo) {
		return mapper.deleteChattingRoom(chattingNo, memberNo);
	}
	
	// 안읽은 채팅 개수 확인
	@Override
	public int getTotalUnreadCount(Long memberNo) {
	    return mapper.getTotalUnreadCount(memberNo);
	}
}
