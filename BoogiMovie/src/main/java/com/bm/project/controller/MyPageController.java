package com.bm.project.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bm.project.dto.MemberDto;
import com.bm.project.service.myPage.MyPageService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/myPage")
@RequiredArgsConstructor
@SessionAttributes("loginMember")
public class MyPageController {
	
	private final MyPageService service;
	
	// 마이페이지 (프로필 관리)
	@GetMapping
	public String myInfo(@SessionAttribute("loginMember") MemberDto.LoginResult loginMember,
						 Model model) {
		
		// 현재 로그인된 회원의 정보 및 소장한 영화 가져오기
		MemberDto.MemberInfo memberInfo = service.getMemberInfo(loginMember.getMemberNo());
		System.out.println("회원 정보\n" + memberInfo);
		model.addAttribute("memberInfo", memberInfo);
		
		// 사이드바 선택 효과
		model.addAttribute("sideBar", "info");
		
		return "myPage/myPage_info";
	}
	
	// 내가 찜한 상품
	@GetMapping("/likedList")
	public String likedList(Model model) {
		
		// 사이드바 선택 효과
		model.addAttribute("sideBar", "liked");
		return "myPage/myFavorite";
	}
	
	// 회원 탈퇴 창으로 이동
	@GetMapping("/secession")
	public String secession(Model model) {
		
		// 사이드바 선택 효과
		model.addAttribute("sideBar", "secession");
		return "myPage/secession";
	}
	
	// 회원 탈퇴 처리
	@PostMapping("/secession")
	public String secession(@SessionAttribute("loginMember") MemberDto.LoginResult loginMember,
							Model model,
							RedirectAttributes ra,
							SessionStatus status, HttpServletResponse response) {
		
		int secession = service.secession(loginMember.getMemberNo());
		
		SecurityContextHolder.clearContext(); // 시큐리티 컨텍스트 비우기
	    status.setComplete(); // 세션 만료
	    
		// 쿠키 삭제
		Cookie cookie = new Cookie("saveId", "");
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie); // 쿠키 적용
	    
	    ra.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다.");
	    return "redirect:/";
	}

}
