package com.bm.project.jwt.model.service;

import com.bm.project.jwt.model.dto.AdminDto;
import com.bm.project.jwt.model.dto.JwtToken;

public interface JwtService {

	/** 관리자 로그인 인증 및 토큰 생성
	 * @param adminDto
	 * @return JwtToken
	 */
	JwtToken adminLogin(AdminDto.AdminResponse adminDto);



}
