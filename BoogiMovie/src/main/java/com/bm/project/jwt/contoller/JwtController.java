package com.bm.project.jwt.contoller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bm.project.jwt.model.dto.AdminDto;
import com.bm.project.jwt.model.dto.JwtToken;
import com.bm.project.jwt.model.service.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


/*
 * 관리자 전용 로그인 (JWT 토큰 기반) 
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class JwtController {
	private final JwtService adminService;
	
	// 회원 로그인에서 관리자 로그인 페이지로 이동 요청
	@GetMapping("/login")
	public String adminLogin() {
		return "admin/login_admin"; // login_admin.html
	}
	
	// 관리자 로그인
	@PostMapping("/login")
	public String adminLogin(AdminDto.AdminResponse adminDto, 
							 HttpServletResponse response,
							 RedirectAttributes ra) {		
		try {
			// 로그인 인증 및 토큰 생성
			JwtToken adminToken = adminService.adminLogin(adminDto);
			
			// 쿠키에 Access 토큰 저장
			Cookie accessCookie = new Cookie("accessToken", adminToken.getAccessToken());
			accessCookie.setHttpOnly(true);  // 자바스크립트 접근 방지
			accessCookie.setPath("/"); 	   // 모든 경로에서 쿠키 전송
			accessCookie.setMaxAge(60 * 60); // 1시간
			response.addCookie(accessCookie);
			
			// Refresh 토큰도 함께 저장
			Cookie refreshCookie = new Cookie("refreshToken", adminToken.getRefreshToken());
			refreshCookie.setHttpOnly(true);
			refreshCookie.setPath("/");
			refreshCookie.setMaxAge(60 * 60);
			response.addCookie(refreshCookie);
			
			// 관리자 회원 번호도 쿠키에 저장
			Cookie adminNoCookie = new Cookie("adminNo", String.valueOf(adminToken.getAdminNo()));
			adminNoCookie.setHttpOnly(true);
			adminNoCookie.setPath("/");
			adminNoCookie.setMaxAge(60 * 60);
			response.addCookie(adminNoCookie);
			
			ra.addFlashAttribute("message", "관리자 페이지 로그인 성공");
			return "redirect:/admin/statistics";
			
		} catch (IllegalArgumentException e) {
			// 로그인 실패시
			ra.addFlashAttribute("message", e.getMessage());
			return "redirect:/admin/login";
		}
	}
	
	// 관리자 로그아웃
	@GetMapping("/logout")
	public String adminLogout(HttpServletResponse response, HttpServletRequest request) {
		// 현재 로그인한 관리자 정보 가져오기
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		HttpSession session = request.getSession(false);
	    if (session != null) {
	        session.invalidate(); // 세션도 꼬일 수 있으니 날리기
	    }
		
		if(auth != null && auth.isAuthenticated()) {
			adminService.deleteRefreshToken(auth.getName());
			
			// 시큐리티 컨텍스트 비워주기
			SecurityContextHolder.clearContext();
			
			// 쿠키도 삭제
			deleteCookie(response, "accessToken");
		    deleteCookie(response, "refreshToken");
		}
		return "redirect:/admin/login";
	}
	
	// 쿠키 삭제
	private void deleteCookie(HttpServletResponse response, String cookieName) {
	    Cookie cookie = new Cookie(cookieName, null);
	    cookie.setPath("/");
	    cookie.setMaxAge(0); // 즉시 만료
	    cookie.setHttpOnly(true);
	    response.addCookie(cookie);
	}
	

}
