package com.bm.project.repository;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bm.project.entity.Movie;
import com.bm.project.entity.Product;

public interface MovieRepositoryCustom{

	// 영화 목록 조회
	Page<Movie> selectMovieList(Map<String, Object> paramMap, Pageable pageable);

	// 영화 검색 조회
	Page<Movie> searchMovieList(Map<String, Object> paramMap, Pageable pageable);


}
