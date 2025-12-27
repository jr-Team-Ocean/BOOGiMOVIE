package com.bm.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

	// 통계 화면 보여주기
	@GetMapping("/statistics")
	public String statistics() {
		return "admin/statistics";
	}
}
