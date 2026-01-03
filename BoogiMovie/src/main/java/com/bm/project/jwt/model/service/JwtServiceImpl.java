package com.bm.project.jwt.model.service;


import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.bm.project.entity.Member;
import com.bm.project.jwt.model.dto.AdminDto;
import com.bm.project.jwt.model.dto.JwtToken;
import com.bm.project.jwt.provider.JwtTokenProvider;
import com.bm.project.jwt.repository.JwtRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
	private final JwtRepository adminRepository;
	private final BCryptPasswordEncoder bcrypt;
	private final JwtTokenProvider jwtTokenProvider;
	

	// 관리자 로그인 인증 및 토큰 생성
	@Override
	public JwtToken adminLogin(AdminDto.AdminResponse adminDto) {
		// 1. 로그인 정보가 일치하는지 DB에서 조회
		Member admin = adminRepository.findByMemberId(adminDto.getAdminId())
						.orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
									
		// 회원 정보를 가져온 경우
		// 만약 일반 회원이라면
		if (admin.getIsAdmin() == Member.IsYN.N) { 
		    throw new IllegalArgumentException("관리자 권한이 없습니다.");
		}
		
		// 비밀번호 검증
		if(!bcrypt.matches(adminDto.getAdminPw(), admin.getMemberPw())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}
		
		// 관리자 인증
		Authentication authentication = new UsernamePasswordAuthenticationToken(admin.getMemberId(), 
																				null, 
																				List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
																				// 권한이 여러 개일 수도 있으므로 List로 담아 보냄
		
		// AccessToken 및 RefreshToken 생성
		return jwtTokenProvider.createToken(authentication);
	}


	// 관리자 로그아웃
	@Override
	public void deleteRefreshToken(String name) {
		jwtTokenProvider.deleteRefreshToken(name);
		
	}

}
