package com.bm.project.controller;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bm.project.dto.MemberDto;
import com.bm.project.dto.MemberDto.Login;
import com.bm.project.dto.MemberDto.LoginResult;
import com.bm.project.entity.Member;
import com.bm.project.service.member.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@SessionAttributes("loginMember")
public class MemberController {
	
	private final MemberService memberService;
	
	private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
	
	// 로그인 화면 이동
	@GetMapping("/login")
	public String login() {
		return "member/login";
	}
	
	
	// 로그인 
	@PostMapping("/login")
	public String login(Model model, MemberDto.Login loginDto
			, @RequestHeader(value = "referer", required = false) String referer
			, @RequestParam(value = "saveId", required = false) String saveId
			, HttpServletResponse response
			, HttpServletRequest request
			, RedirectAttributes ra) {
		
		LoginResult loginMember = memberService.login(loginDto);
		
		String path = "redirect:";
		
		if(loginMember != null) {
			path += "/";
			
			model.addAttribute("loginMember", loginMember);
			
			// 시큐리티 추가: 일반 회원 권한 부여
			SecurityContext context = SecurityContextHolder.createEmptyContext();
	        
			// 인증 객체 생성
			Authentication authentication = new UsernamePasswordAuthenticationToken(
	                loginMember.getMemberId(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
	        
	        // 컨텍스트에 인증 정보 설정
	        context.setAuthentication(authentication);
	        SecurityContextHolder.setContext(context);
	        
	        // 세션에 컨텍스트 영구 저장 (리다이렉트 시에도 유지함)
	        securityContextRepository.saveContext(context, request, response);
			
			// 쿠키 생성
			Cookie cookie = new Cookie("saveId", loginMember.getMemberId());
			
			if(saveId != null) {
				cookie.setMaxAge(60 * 60 * 24 * 30);
				
			}else {
				cookie.setMaxAge(0);
			}
			
			cookie.setPath("/");
			response.addCookie(cookie);
			
		}else {
			path += referer;
			ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
		}
		
		return path;
	}
	
	
	// 로그아웃
	@GetMapping("logout")
	public String logout(SessionStatus status, RedirectAttributes ra) {
		status.setComplete();
		SecurityContextHolder.clearContext(); // 컨텍스트에서도 제거
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
			message = "회원가입에 실패했습니다.";
		}
		     
		ra.addFlashAttribute("message", message);
		
		return path;
	}
	
	
	
	
	
	
	
}
