package com.bm.project.repository;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.bm.project.entity.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class BookRepositoryImpl implements BookRepository{
	
	@PersistenceContext
	private EntityManager em;

	// 도서 목록 조회
	@Override
	public Page<Product> selectBookList(Map<String, Object> paramMap, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
