package com.bm.project.repository;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bm.project.entity.Product;

public interface BookRepository {

	// 도서 목록 조회
	Page<Product> selectBookList(Map<String, Object> paramMap, Pageable pageable);

}
