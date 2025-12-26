package com.bm.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bm.project.service.member.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
public class HomeController {

	// 홈 화면 이동
	@RequestMapping("/")
	public String homeForward() {
		return "common/home";
	}
}
