package com.bm.project.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bm.project.dto.BookDto;

public interface BookService {

	// 도서 목록 조회
	Page<BookDto.Response> selectBookList(Map<String, Object> paramMap, Pageable pageable);

	// 검색용 도서 목록 조회
	Page<BookDto.Response> searchBookList(Map<String, Object> paramMap, Pageable pageable);

}
