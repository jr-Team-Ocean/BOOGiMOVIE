package com.bm.project.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bm.project.entity.Product;

public interface BookRepository {

	// 도서 목록 조회
	Page<Product> selectBookList(Map<String, Object> paramMap, Pageable pageable);

	// 저자조회용
	List<Object[]> selectWritersByProductNos(List<Long> productNos);

	Page<Product> searchBookList(Map<String, Object> paramMap, Pageable pageable);

}
