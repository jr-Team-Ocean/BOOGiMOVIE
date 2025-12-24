package com.bm.project.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bm.project.dto.BookDto;
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
			@PageableDefault(size = 20, page=0)Pageable pageable,
			Model model
			) {
		Page<BookDto.Response> page;
		/* 
		 * 검색 목록 조회 : 페이지 + 정렬 + 장르 + 검색어
		 * 				/books?category=10&key=홍길동&page=1&sort=latest
		 * 장르 목록 조회 : 페이지 + 정렬 + 장르
		 * 				/books?category=10&page=2&sort=latest
		 * 전체 목록 조회 : 페이지 + 정렬
		 * 				/books?page=1&sort=latest
		 * 
		 * */
		
		// 검색 여부
		if (paramMap.get("query") == null ||paramMap.get("query").toString().isBlank() ) {
			
			// 검색 없는 경우
			page = bookService.selectBookList(paramMap, pageable);
			
			
		} else {
			
			// 검색 있음
			page = bookService.searchBookList(paramMap, pageable);
		}
		
		model.addAttribute("page", page);
		model.addAttribute("paramMap", paramMap);
		
		
		
		return "book/bookList";
	}
	
	
	
	
	
	
	
	
	
}
