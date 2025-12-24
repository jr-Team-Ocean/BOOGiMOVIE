package com.bm.project.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bm.project.dto.BookDto.Response;
import com.bm.project.entity.Product;
import com.bm.project.repository.BookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl  implements BookService {
	
	private final BookRepository bookRepository;
	
	
	// 도서 목록 조회
	@Override
	public Page<Response> selectBookList(Map<String, Object> paramMap, Pageable pageable) {
		
		Page<Product> page = bookRepository.selectBookList(paramMap,pageable);
		
		return null;
	}

	@Override
	public Page<Response> searchBookList(Map<String, Object> paramMap, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
