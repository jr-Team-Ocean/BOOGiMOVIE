package com.bm.project.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bm.project.dto.MemberDto;
import com.bm.project.dto.MemberDto.LoginResult;
import com.bm.project.service.myPage.MyPageService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;
import com.bm.project.dto.PageDto;

import java.util.Map;
import java.util.HashMap;

import org.springframework.http.HttpStatus;

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
	
	// 내가 찜한 상품 (페이지 이동)
	@GetMapping("/likedList")
	public String likedList(
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @SessionAttribute(value = "loginMember", required = false) LoginResult loginMember,
	        Model model) {
	    
	    if (loginMember == null) return "redirect:/member/login";

	    // 1. Pageable 생성
	    Pageable pageable = PageRequest.of(page - 1, 10);
	    
	    // 2. 서비스 호출 (반환 타입을 MemberDto.FavoriteResponse로 변경)
	    Page<MemberDto.FavoriteResponse> resultPage = service.getFavoriteList(loginMember.getMemberNo(), "recent", pageable);
	    
	    // 3. PageDto에 담기
	    PageDto<MemberDto.FavoriteResponse> pageDto = new PageDto<>(resultPage);
	    
	    model.addAttribute("favorites", resultPage.getContent());
	    model.addAttribute("pageDto", pageDto);
	    model.addAttribute("sideBar", "liked"); 
	    
	    return "myPage/myFavorite";
	}

	// 내가 찜한 상품 비동기 데이터 (정렬 및 페이징 클릭 시)
	@GetMapping("/searchResult")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> searchResult(
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "order", defaultValue = "recent") String order,
	        @SessionAttribute(value = "loginMember", required = false) LoginResult loginMember) {

	    Map<String, Object> response = new HashMap<>();

	    if (loginMember == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

	    Pageable pageable = PageRequest.of(page - 1, 10);
	    
	    // 서비스 호출 (Favorite 대신 MemberDto.FavoriteResponse 사용)
	    Page<MemberDto.FavoriteResponse> resultPage = service.getFavoriteList(loginMember.getMemberNo(), order, pageable);
	    
	    // 친구의 PageDto 활용
	    PageDto<MemberDto.FavoriteResponse> pageDto = new PageDto<>(resultPage);

	    response.put("favorites", resultPage.getContent());
	    response.put("pageDto", pageDto);

	    return ResponseEntity.ok(response);
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
