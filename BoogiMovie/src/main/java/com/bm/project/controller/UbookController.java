package com.bm.project.controller;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
		
		Page<UbookDto.Response> pageResp;
		
		Pageable pageable = PageRequest.of(page - 1, 20);
		
		pageResp = UbookService.selectbookList(paramMap, pageable);
		
		model.addAttribute("url", "ubooks");
		
		return "usedBook/usedBook_List";
	}

}
