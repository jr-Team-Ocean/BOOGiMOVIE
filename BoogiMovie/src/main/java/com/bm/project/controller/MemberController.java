package com.bm.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bm.project.dto.MemberDto;
import com.bm.project.service.member.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@SessionAttributes("loginMember")
public class MemberController {
	
	private final MemberService memberService;
	
	// 로그인 화면 이동
	@GetMapping("/login")
	public String login() {
		return "member/login";
	}
	
	// 로그아웃
	@GetMapping("logout")
	public String logout(SessionStatus status) {
		status.setComplete();
		return "redirect:/";
	}
	
	
	// 회원 가입 화면 이동
	@GetMapping("/signUp")
	public String signUp() {
		return "member/signUp";
	}
	
	// 회원 가입(등록)
	@PostMapping("/signUp")
	public String addMember(
			@ModelAttribute MemberDto.Create creatDto,
			@RequestParam String[] memberAddress,
			RedirectAttributes ra){
		
		// 우편번호/도로명/상세주소 구분하기 위해서 구분자(^^^) 추가
		String address = String.join("^^^", memberAddress);
		creatDto.setMemberAddress(address);
		
		// 회원 가입 서비스 호출
		String memberId = memberService.createMember(creatDto);
		
		String path = "redirect:";
		String message = null;
		
		if(!memberId.isEmpty()) {
			path += "/member/login";
			message = creatDto.getMemberNickName() + "님의 가입을 환영합니다.";
		
		}else {
			path += "/member/signUp";
			message = "회원 가입의 실패했습니다.";
		}
		     
		ra.addFlashAttribute("message", message);
		
		return path;
	}
	
	
	
	
	
	
	
}
