package com.bm.project.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bm.project.dto.BookDto;
import com.bm.project.dto.PageDto;
import com.bm.project.service.BookService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
	
	private final BookService bookService;
	
	@GetMapping
	public String selectBookList(
			@RequestParam Map<String, Object> paramMap,
			@RequestParam(name = "page", defaultValue = "1") int page,
			
			Model model
			) {
		Page<BookDto.Response> pageResp;
		
		
		Pageable pageable = PageRequest.of(page - 1, 20);
		/* 
		 * 검색 목록 조회 : 페이지 + 정렬 + 장르 + 검색어
		 * 				/books?page=1&category=10&query=홍길동&sort=latest
		 * 장르 목록 조회 : 페이지 + 정렬 + 장르
		 * 				/books?page=2&category=10&sort=latest
		 * 전체 목록 조회 : 페이지 + 정렬
		 * 				/books?page=1&sort=latest
		 * 
		 * */
		
		// 검색 여부
		if (paramMap.get("query") == null ||paramMap.get("query").toString().isBlank() ) {
			
			// 검색 없는 경우
			pageResp = bookService.selectBookList(paramMap, pageable);
			
			
		} else {
			
			// 검색 있음
			pageResp = bookService.searchBookList(paramMap, pageable);
		}
		
		
		PageDto<BookDto.Response> pageDto = new PageDto<>(pageResp);
		
		
		model.addAttribute("page", pageResp);
		model.addAttribute("pageDto", pageDto);
		model.addAttribute("paramMap", paramMap);
		
		
		
		return "book/bookList";
	}
	
	
	@GetMapping("/{productNo}")
	public String selectBookDetail(
			@PathVariable("productNo") Long productNo,
			Model model
			
			) {
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
