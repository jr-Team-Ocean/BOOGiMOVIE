package com.bm.project.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bm.project.dto.BookDto;
import com.bm.project.dto.BookDto.Create;
import com.bm.project.dto.BookDto.Update;

public interface BookService {

	// 도서 목록 조회
	Page<BookDto.Response> selectBookList(Map<String, Object> paramMap, Pageable pageable);

	// 검색용 도서 목록 조회
	Page<BookDto.Response> searchBookList(Map<String, Object> paramMap, Pageable pageable);

	// 도서 상세 조회
	BookDto.Response selectBookDetail(Long productNo);

	// 도서 등록 (상품 번호 반환)
	Long bookWrite(Create bookCreate) throws IllegalStateException, IOException;

	// 도서 수정
	void bookUpdate(Long productNo, Update bookUpdate) throws IllegalStateException, IOException;

	// 삭제
	void bookDelete(Long productNo);

}
