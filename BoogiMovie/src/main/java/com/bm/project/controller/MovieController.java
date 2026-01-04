package com.bm.project.controller;

import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bm.project.dto.MovieDto;
import com.bm.project.dto.PageDto;
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
	@GetMapping("/{productNo}")
	public String getMovieDetail(@PathVariable("productNo") Long productNo, Model model) {
		
		MovieDto.Response movie = movieService.getMovieDetail(productNo);
		
		model.addAttribute("movie", movie);
		model.addAttribute("url", "movies");
		
		return "movie/movieDetail";
	}
	
	// 영화 등록 화면 이동
	@PostMapping("/write")
	public String movieWrite() {
		return "movie/movieWrite";
	}
}
