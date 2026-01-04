package com.bm.project.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bm.project.dto.BookDto;
import com.bm.project.dto.MemberDto.LoginResult;
import com.bm.project.dto.PageDto;
import com.bm.project.service.BookService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
	
	private final BookService bookService;
	
	// 도서 목록 조회
	@GetMapping
	public String selectBookList(
			@RequestParam Map<String, Object> paramMap,
			@RequestParam(name = "page", defaultValue = "1") int page,
			// @SessionAttribute(value = "loginMember", required = false) LoginResult loginMember,
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
		model.addAttribute("url", "books");
		
		
		return "book/bookList";
	}
	
	
	// 도서 상세 조회
	@GetMapping("/{productNo}")
	public String selectBookDetail(
			@PathVariable("productNo") Long productNo,
			Model model
			) {
		BookDto.Response book = bookService.selectBookDetail(productNo);
		
		model.addAttribute("book", book);
		model.addAttribute("url", "books");
		
		return "book/bookDetail";
	}
	
	
	
	// 도서 등록 화면 전환
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/write")
	public String bookInsert() {
		return "book/bookWrite";
	}
	
	// 도서 등록
	@PostMapping("/write")
	public String bookInsert(
			@ModelAttribute BookDto.Create bookCreate,
			// @RequestParam(value = "bookImage", required = true) MultipartFile image,
			RedirectAttributes ra
			) throws IllegalStateException, IOException {
		
		Long productNo = bookService.bookWrite(bookCreate);
		
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
	
	
	// 도서 수정 화면 전환
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{productNo}/update")
	public String bookUpdate(
			@PathVariable Long productNo,
            Model model
			) {
		BookDto.Response book = bookService.selectBookDetail(productNo);
        model.addAttribute("book", book);
		
		return "book/bookUpdate";
	}
	
	// 도서 수정
	@PostMapping("/{productNo}/update")
	public  String bookUpdate(
			@PathVariable("productNo") Long productNo,
			@ModelAttribute BookDto.Update bookUpdate,
			RedirectAttributes ra
			) throws IllegalStateException, IOException {
		
		bookService.bookUpdate(productNo, bookUpdate);
		
		
		
		String message = null;
		String path = "redirect:";
		if (productNo > 0) {

			path += "/books/" + productNo; 
			message = "도서 상품이 수정되었습니다.";
			
		} else {
			path += "update";
			message = "도서 수정에 실패하였습니다.";
		}
		
		ra.addFlashAttribute("message", message);
		return path;
	}
	
	
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/{productNo}/delete")
	public String bookDelete(
	        @PathVariable Long productNo,
	        RedirectAttributes ra
			) {
	    bookService.bookDelete(productNo);
	    
	    String message = "게시글이 삭제되었습니다.";
 		String path = "redirect:/books";
	 		
		ra.addFlashAttribute("message", message);
	    return path;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
