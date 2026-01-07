package com.bm.project.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bm.project.dto.PageDto;
import com.bm.project.dto.UbookDto;
import com.bm.project.service.UbookService;
import com.solapi.shadow.retrofit2.http.POST;
import com.solapi.shadow.retrofit2.http.PUT;

import lombok.RequiredArgsConstructor;
import lombok.ToString;


@ToString
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
	
	
	// 중고도서 상세 조회
	@GetMapping("/{productNo}")
	public String selectUbookDetail (
			
			@PathVariable("productNo") Long productNo,
			Model model
			
			) {
		System.out.println("test");
		UbookDto.Response ubook = ubookService.selectUbookDetail(productNo);
		System.out.println(productNo);
		System.out.println(ubook);
		
		model.addAttribute("ubook", ubook);
		model.addAttribute("url", "ubooks");
		
		return "usedBook/usedBook_Detail";
		
		
	}
	
	// 중고도서 삭제
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/{productNo}")
	public String deleteProduct(
			
			@PathVariable("productNo") Long productNo,
			RedirectAttributes ra
			
			){
		
		System.out.println("productNo :" + productNo);
		
		ubookService.deleteProduct(productNo);
		
		String path = "redirect:/ubooks";
		
		return path;
		
	}
	
	
	// 중고도서 등록화면 전환
	@GetMapping("/enroll")
	public String goEnrollUbook() {
		
		System.out.println("가가나나나나나나나");
		
		return "usedBook/usedBook_Enroll";
	}
	
	
	// 중고도서 등록
	@PostMapping("/insert")
	public String UbookInsert(
			
			@ModelAttribute UbookDto.Create createUbook,
			
			RedirectAttributes ra
			
			) throws IllegalStateException, IOException {
		
		System.out.println(createUbook);
		
		Long productNo = ubookService.insertUbook(createUbook);
		
		String message = null;
		String path = "redirect:";
		if (productNo > 0) {
			// 게시글 삽입 성공 시
			// -> 방금 삽입한 게시글의 상세 조회 페이지로 리다이렉트	
			path += "/books/" + productNo; 
			message = "도서 상품이 등록되었습니다.";
			
		} else {
			// 게시글 삽입 실패 시
			// -> 게시글 작성 페이지로 리다이렉트

			// ==> 작성하는 요청 주소와 리다이렉트 할 주소가 똑같음 = 상대주소로 
			path += "write";
			message = "도서 등록에 실패하였습니다.";
		}
		
		ra.addFlashAttribute("message", message);
		
		return path;
		
		
				
		
		
	}
	
	

}
