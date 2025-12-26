package com.bm.project.jwt.contoller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bm.project.jwt.model.dto.AdminDto;
import com.bm.project.jwt.model.dto.JwtToken;
import com.bm.project.jwt.model.service.AdminService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


/*
 * 관리자 전용 로그인 (JWT 토큰 기반) 
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	private final AdminService adminService;
	
	// 회원 로그인에서 관리자 로그인 페이지로 이동 요청
	@GetMapping("/login")
	public String adminLogin() {
		return "admin/login_admin"; // login_admin.html
	}
	
	// 관리자 로그인
	@PostMapping("/login")
	public String adminLogin(AdminDto.AdminResponse adminDto, HttpServletResponse response) {
		// 로그인 인증 및 토큰 생성
		JwtToken adminToken = adminService.adminLogin(adminDto);
		
		// 쿠키에 토큰 저장
		Cookie cookie = new Cookie("accessToken", adminToken.getAccessToken());
		cookie.setHttpOnly(true);  // 자바스크립트 접근 방지
		cookie.setPath("/"); 	   // 모든 경로에서 쿠키 전송
		cookie.setMaxAge(60 * 60); // 1시간
		response.addCookie(cookie);
		
		return "admin/statistics";
	}
}
