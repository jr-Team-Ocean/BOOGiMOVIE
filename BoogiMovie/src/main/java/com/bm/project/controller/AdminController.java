package com.bm.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

	// 통계 화면 보여주기
	@GetMapping("/statistics")
	public String statistics(Model model) {
		model.addAttribute("activeMenu", "statistics");
		return "admin/statistics";
	}
	
	// 신고 관리 화면 보여주기
	@GetMapping("/report")
	public String report(Model model) {
		model.addAttribute("activeMenu", "report");
		return "admin/review_report";
	}
	
	// 1:1 문의
}
