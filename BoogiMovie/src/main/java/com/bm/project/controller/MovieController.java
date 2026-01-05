package com.bm.project.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bm.project.dto.MovieDto;
import com.bm.project.dto.PageDto;
import com.bm.project.entity.Product;
import com.bm.project.service.MovieService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

	private final MovieService movieService;
	
	// 영화 목록조회
	@GetMapping
	public String selectMovieList(
			@RequestParam Map<String, Object> paramMap,
			@RequestParam(name="page", defaultValue="1") int page,
			Model model) {
		
		// page 음수/0 방지 (UI는 1부터)
        int safePage = Math.max(page, 1);
		
		// sort 기본값
		String sort = (paramMap.get("sort") == null || paramMap.get("sort").toString().isBlank())
				?"latest" : paramMap.get("sort").toString();
		
		paramMap.put("sort", sort);
		
		PageDto<MovieDto.Response> moviePage; 
		
		Pageable pageable = PageRequest.of(safePage - 1, 20);
		
		// 검색
		String query = (paramMap.get("query") == null) ? "" : paramMap.get("query").toString().trim();
		
		// 전체 목록
		if (query.isBlank()) {
            moviePage = movieService.selectMovieList(paramMap, pageable);
            
        } else {
        	// 검색 목록
            paramMap.put("query", query);
            moviePage = movieService.searchMovieList(paramMap, pageable);
        }
		
		
		model.addAttribute("pageDto", moviePage);
		model.addAttribute("paramMap", paramMap);
		model.addAttribute("sort", sort);
		model.addAttribute("url", "movies");
		
		return "movie/movieList";
	}
		
	
	// 영화 상세정보
	@GetMapping("/{productNo:[0-9]+}")
	public String getMovieDetail(@PathVariable("productNo") Long productNo, Model model) {
		
		MovieDto.Response movie = movieService.getMovieDetail(productNo);
		
		model.addAttribute("movie", movie);
		model.addAttribute("url", "movies");
		
		return "movie/movieDetail";
	}
	
	// 영화 등록 화면 이동
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/write")
	public String createMovie() {
		return "movie/movieWrite";
	}
	
	// 영화 등록
	@PostMapping("/write")
	public String createMovie(
			@ModelAttribute MovieDto.Create movieCreate,
			@RequestParam("movieType") Long movieType,   // 100 or 200
            @RequestParam("genreType") Long genreType,   // 1~19
			RedirectAttributes ra) throws IllegalStateException, IOException {
		
		movieCreate.setCategoryId(movieType + genreType);
		
		Long productNo = movieService.createMovie(movieCreate);
		
		String message = null;
		String path = "redirect:";
		
		if(productNo > 0) {
			// 등록 성공시 -> 상세 조회 페이지로 이동
			path += "/movies/" + productNo;
			message = "영화 상품이 등록되었습니다.";
			
		}else {
			// 등록 실패시 -> 작성 페이지로 리다이렉트
			path += "write";
			message = "영화 상품 등록에 실패하였습니다.";
		}
		
		ra.addFlashAttribute("message", message);
		
		return path;
	}
	
	// 영화 수정 화면 전환
	// @PreAuthorize("hasRole('ADMIN')")
	@GetMapping("{productNo:[0-9]+}/update")
	public String updateMovie(
			@PathVariable("productNo") Long productNo,
			Model model) {
		
		MovieDto.Response movie = movieService.getMovieDetail(productNo);
		model.addAttribute("movie", movie);
		
		return "movie/movieUpdate";
	}
	
	// 영화 수정
	@PostMapping("{productNo:[0-9]+}/update")
	public String updateMovie(
			@PathVariable("productNo") Long productNo,
			@ModelAttribute MovieDto.Update movieUpdate,
			RedirectAttributes ra) throws IllegalStateException, IOException {
		
		movieService.updateMovie(productNo, movieUpdate);
		
		String message = null;
		String path = "redirect:";
		
		if(productNo > 0) {
			// 등록 성공시 -> 상세 조회 페이지로 이동
			path += "/movies/" + productNo;
			message = "영화 상품이 수정되었습니다.";
			
		}else {
			// 등록 실패시 -> 작성 페이지로 리다이렉트
			path += "update";
			message = "영화 상품 수정에 실패하였습니다.";
		}
		
		ra.addFlashAttribute("message", message);
		
		return path;
	}
	
	// 영화 삭제
	@GetMapping("{productNo:[0-9]+}/delete")
	public String deleteMovie(
			@PathVariable("productNo") Long productNo,
			RedirectAttributes ra) {
		
		movieService.deleteMovie(productNo);
		
		String message = "영화 상품이 삭제되었습니다.";
 		String path = "redirect:/movies";
	 		
		ra.addFlashAttribute("message", message);
		
		return path;
	}
}
