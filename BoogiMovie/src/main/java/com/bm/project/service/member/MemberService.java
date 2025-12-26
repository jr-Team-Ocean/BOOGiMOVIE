package com.bm.project.service.member;

import com.bm.project.dto.MemberDto;
import com.bm.project.dto.MemberDto.Create;
import com.bm.project.dto.MemberDto.Login;
import com.bm.project.dto.MemberDto.LoginResult;
import com.bm.project.entity.Member;

public interface MemberService {

	// 회원 등록
	String createMember(Create creatDto);

	// 로그인
	MemberDto.LoginResult login(Login loginDto);

}
