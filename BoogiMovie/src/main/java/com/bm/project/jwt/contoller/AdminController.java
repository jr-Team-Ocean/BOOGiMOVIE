package com.bm.project.jwt.contoller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bm.project.jwt.model.service.AdminService;

import lombok.RequiredArgsConstructor;


/*
 * 관리자 전용 로그인 (JWT 토큰 기반) 
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
//	private final AdminService adminService;
	
	// 회원 로그인에서 관리자 로그인 페이지로 이동 요청
	@GetMapping("/login")
	public String adminLogin() {
		return "admin/login_admin"; // login_admin.html
	}
}
