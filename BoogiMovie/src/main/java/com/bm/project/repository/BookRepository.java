package com.bm.project.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bm.project.entity.Book;
import com.bm.project.entity.Product;

public interface BookRepository {

	// 도서 목록 조회
	Page<Product> selectBookList(Map<String, Object> paramMap, Pageable pageable);

	// 저자조회용
	List<Object[]> selectWritersByProductNos(List<Long> productNos);
	
	// 도서 검색 조회
	Page<Product> searchBookList(Map<String, Object> paramMap, Pageable pageable);

	
	// 도서 상세정보 조회
	Optional<Book> selectBookDetailByProductNo(Long productNo);
	
	// 도서 상세정보 저자 조회용
	List<String> selectWritersByProductNo(Long productNo);
	
	// 도서 상세정보 출판사 조회용
	List<String> selectPublishersByProductNo(Long productNo);

	
	
	
}
