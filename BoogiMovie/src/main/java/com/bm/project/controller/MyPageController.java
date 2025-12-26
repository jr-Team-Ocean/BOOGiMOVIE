package com.bm.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/myPage")
public class MyPageController {
	
	// 마이페이지 이동
	@GetMapping
	public String myInfo() {
		return "myPage/myPage_info";
	}

}
