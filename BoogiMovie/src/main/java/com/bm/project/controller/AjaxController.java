package com.bm.project.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bm.project.service.member.AjaxService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dupCheck")
@RequiredArgsConstructor
public class AjaxController {

	private final AjaxService ajaxService;
	
	// 아이디 중복 검사
	@GetMapping("/id")
	public boolean checkId(@RequestParam String id){
		boolean duplicated = ajaxService.checkId(id);
		return duplicated;
	}
	
	// 닉네임 중복 검사
	@GetMapping("/nickname")
	public boolean checkNickname(@RequestParam String nickname){
		boolean duplicated = ajaxService.checkNickname(nickname);
		return duplicated;
	}
	
	// 전화번호 중복 검사
	@GetMapping("/phone")
	public boolean checkPhone(@RequestParam String phone){
		boolean duplicated = ajaxService.checkPhone(phone);
		return duplicated;
	}
}
