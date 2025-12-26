package com.bm.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bm.project.service.member.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
public class HomeController {

	// 홈 화면 이동
	@RequestMapping("/")
	public String homeForward(Model model) {
		model.addAttribute("url", "home");
		return "common/home";
	}
}
