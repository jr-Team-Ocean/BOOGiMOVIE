package com.bm.project.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.bm.project.entity.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class UbookRepositoryImpl implements UbookRepository{

	@PersistenceContext
	private EntityManager em;
	
	// 중고도서 목록 조회
	@Override
	public Page<Product> selectbookList(Map<String, Object> paramMap, Pageable pageable) {
		
		String ubookSort = "latest"; // 기본값
		
		String query = "select p " +
						"from Ubook u " +
						"join u.product p " +
						"where p.productDelFl = 'N' " +
						"and p.productType.typeCode = 3" ;
		
		// 정렬
		switch (ubookSort) {
			case "productTitle" : query += " order by p.productTitle asc";
			break;
			
			case "latest": query += " order by p.productDate desc";
			break;
			
			default: query += " order by p.productDate desc";
			break;
		
		}
		
		
		// 조건 없을 경우
		var nQuery = em.createQuery(query, Product.class);
		
		List<Product> ubooks = nQuery.setFirstResult((int)pageable.getOffset())
										// 어디서부터 가지고 올 것인지
										.setMaxResults(pageable.getPageSize())
										// 몇 개를 가지고 올 것인지
										.getResultList();
		
		// 게시글 수
		String countProductQuery = "select count(p) " +
									"from Ubook u " +
									"join u.product p " +
									"where p.productDelFl = 'N' " +
									"and p.productType.typeCode = 1";
				
		
		var mQuery = em.createQuery(countProductQuery, Long.class);
		
		Long totalUbookCount = mQuery.getSingleResult();
						
				
		
		return new PageImpl<>(ubooks, pageable, totalUbookCount);
	}
	

}
