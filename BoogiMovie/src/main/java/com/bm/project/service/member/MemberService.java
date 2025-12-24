package com.bm.project.service.member;

import com.bm.project.dto.MemberDto.Create;

public interface MemberService {

	// 회원 등록
	String createMember(Create creatDto);

}
