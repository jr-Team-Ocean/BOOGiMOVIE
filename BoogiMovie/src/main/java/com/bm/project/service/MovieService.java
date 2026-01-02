package com.bm.project.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bm.project.dto.MovieDto;
import com.bm.project.dto.MovieDto.Response;
import com.bm.project.dto.PageDto;
import com.bm.project.enums.CommonEnums.ProductDelFl;

public interface MovieService {

	// 영화 목록 조회
	PageDto<MovieDto.Response> selectMovieList(Map<String, Object> paramMap, Pageable pageable);

	// 검색 + 영화 목록 조회
	PageDto<MovieDto.Response> searchMovieList(Map<String, Object> paramMap, Pageable pageable);



}
