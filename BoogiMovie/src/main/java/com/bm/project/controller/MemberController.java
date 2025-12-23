package com.bm.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
	
	// 일반 회원 로그인
	@GetMapping("/login")
	public String login() {
		return "member/login";
	}
	
	
	// 회원 가입
	@GetMapping("/signUp")
	public String signUp() {
		return "member/signUp";
	}
}
