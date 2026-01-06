package com.bm.project.chatting.model.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bm.project.chatting.model.dao.ChattingMapper;
import com.bm.project.chatting.model.dto.ChattingRoom;
import com.bm.project.chatting.model.dto.Member_C;
import com.bm.project.chatting.model.dto.ChattingMessage;
import com.bm.project.chatting.model.dto.ChattingImage;
import com.bm.project.common.utility.Util;

import org.springframework.transaction.annotation.Transactional;


@Service
public class ChattingServiceImpl implements ChattingService {

    @Autowired
    private ChattingMapper mapper;

    // 1. 메시지 삽입 (글자 + 이미지 통합)
    // WebsocketHandler에서 호출하며, 이미지가 있다면 msg.getImgPath()에 경로가 담겨있어야 함
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertMessage(ChattingMessage msg) {
        
        // [A 장부 기록] 메시지 테이블(CHATTING_MESSAGE)에 기본 정보 저장
        // MyBatis의 useGeneratedKeys 덕분에 msg.getMessageId()에 자동 생성된 번호가 채워짐
        int result = mapper.insertMessage(msg);

        // [B 장부 기록] 만약 메시지 객체에 이미지 경로가 들어있다면 사진 테이블에도 저장
        if (result > 0 && msg.getImgPath() != null) {
            ChattingImage img = new ChattingImage();
            img.setMessageId(String.valueOf(msg.getMessageId())); // 연결고리(FK)
            img.setChattingImagePath(msg.getImgPath());           // 이미지 주소
            
            result = mapper.insertChattingImg(img);
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

    // --- 아래는 중복 제거 및 유지된 기존 메서드들 ---

    @Override
    public List<ChattingRoom> selectRoomList(Long memberNo) {
        return mapper.selectRoomList(memberNo);
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
        return mapper.updateReadFlag(paramMap);
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
