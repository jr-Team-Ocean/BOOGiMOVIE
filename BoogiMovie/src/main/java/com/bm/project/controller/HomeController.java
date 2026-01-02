package com.bm.project.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bm.project.dto.HomeDto;
import com.bm.project.service.home.HomeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	
	private final HomeService service;

	// 홈 화면 목록 조회
	@GetMapping("/")
	public String homeList(Model model) {
		
		// 결제 많은 도서/영화 목록 조회
		List<HomeDto> popularProducts = service.getPopularProducts();
		
		// 인기 도서 목록 조회
		List<HomeDto> topBooks = service.getTopBooks();
		
		System.out.println("=== 인기 도서 목록 ===\n" + topBooks);
		
		// 인기 영화 목록 조회
		List<HomeDto> topMovies = service.getTopMovies();
		
		System.out.println("=== 인기 영화 목록 ===\n" + topMovies);
		
		model.addAttribute("topBooks", topBooks);
        model.addAttribute("topMovies", topMovies);
		
		// 헤더 선택 효과를 위해 주는 값
		model.addAttribute("url", "home");
		return "common/home";
	}
	
}
