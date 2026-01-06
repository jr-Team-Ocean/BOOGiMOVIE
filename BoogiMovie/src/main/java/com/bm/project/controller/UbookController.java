package com.bm.project.controller;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bm.project.dto.PageDto;
import com.bm.project.dto.UbookDto;
import com.bm.project.service.UbookService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/ubooks")
@RequiredArgsConstructor
public class UbookController {
	
	private final UbookService ubookService;
	
	@GetMapping
	public String selectUbookList(
			@RequestParam Map<String, Object> paramMap,
			@RequestParam(name="page", defaultValue="1") int page,
			Model model
			
			) {
		
		// paramMap : HTML의 name 속성으로 전달받은 url을 spring이 자동으로 채워준다.
		
		Page<UbookDto.Response> pageResp;
		
		// Pageable : 페이지 번호, 페이지 크기, 정렬 정보를 담는 표준 객체
		Pageable pageable = PageRequest.of(page - 1, 20);
		
		pageResp = ubookService.selectbookList(paramMap, pageable);
		
		// 3. PageDto 변환
	    PageDto<UbookDto.Response> pageDto = new PageDto<>(pageResp);
		
		System.out.println(pageResp);
		System.out.println(pageResp.getContent());
		System.out.println(pageable);
		
		
		model.addAttribute("url", "ubooks");
		model.addAttribute("page", pageResp);
		model.addAttribute("paramMap", paramMap);
		model.addAttribute("ubooks", pageResp.getContent());
		model.addAttribute("pageDto", pageDto);
		
		return "usedBook/usedBook_List";
	}
	
	
	@GetMapping("/{productNo}")
	public String selectUbookDetail (
			
			@PathVariable("productNo") Long productNo,
			Model model
			
			) {
		
		UbookDto.Response ubook = ubookService.selectUbookDetail(productNo);
		
		model.addAttribute("ubook", ubook);
		model.addAttribute("url", "ubooks");
		
		return "usedBook/usedBook_Detail";
		
		
	}

}
