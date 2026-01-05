package com.bm.project.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.bm.project.entity.Product;
import com.bm.project.entity.Ubook;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class UbookRepositoryImpl implements UbookRepository{

	@PersistenceContext
	private EntityManager em;
	
	// 중고도서 목록 조회
	@Override
	public Page<Product> selectbookList(Map<String, Object> paramMap, Pageable pageable) {
		
		Long categoryId = null;
		
		String ubookSort = (String) paramMap.getOrDefault("ubookSort", "latest"); // 기본값
		
		
		
		// 정렬선택 확인
		if (paramMap.get("ubookSort") != null) {
			ubookSort = paramMap.get("ubookSort").toString();
	    }
		
		String query = "select p " +
						"from Ubook u " +
						"join u.product p " +
						"where p.productDelFl = 'N' " +
						"and p.productType.typeCode = 3" ;
		
		
		// 장르 여부 확인
		if (paramMap.get("category") != null) {
		    categoryId = Long.valueOf(paramMap.get("category").toString());
		}
		
		
		// 카테고리를 선택했을 경우
		if (categoryId != null && categoryId != 0) {
			
			//  소설/희곡 탭
			if (categoryId == 11 ) {
				query += " and (p.category.categoryId = :categoryId "
	                   + "or p.category.pCategoryId.categoryId = :categoryId)";
			
			} else { // 나머지
				query += " and p.category.categoryId = :categoryId";
			}
		}
		
		
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
		TypedQuery<Product> nQuery = em.createQuery(query, Product.class);

		
		// 카테고리 선택시 
		if (categoryId != null && categoryId != 0) {
			nQuery.setParameter("categoryId", categoryId);
		}
		
		
		
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
									"and p.productType.typeCode = 3";
				
		
		
		// 카테고리 선택시 
		if (categoryId != null && categoryId != 0) {
			
			if (categoryId == 11) {
				countProductQuery += " and (p.category.categoryId = :categoryId "
						    + "or p.category.pCategoryId.categoryId = :categoryId)";
			} else {				
				countProductQuery += " and p.category.categoryId = :categoryId";
			}
	    }
		
		TypedQuery<Long> mQuery = em.createQuery(countProductQuery, Long.class);

		
		if (categoryId != null && categoryId != 0) {
			mQuery.setParameter("categoryId", categoryId);
        }
		
		
		
		Long totalUbookCount = mQuery.getSingleResult();
						
				
		
		return new PageImpl<>(ubooks, pageable, totalUbookCount);
	}

	
	// 중고도서 상태 조회
	@Override
	public List<Object[]> selectUbookStateList(List<Long> productNos) {
		
		String query = "select p.productNo, u.ubookStatus " +
						"from Ubook u " +
						"join u.product p " +
						"where p.productNo IN :productNos " +
						"and p.productDelFl = 'N' " ;
		
		List<Object[]> result = em.createQuery(query, Object[].class)
                .setParameter("productNos", productNos)
                .getResultList();
		
		
		return result;
	}


	// 중고도서 상세조회 조회 상품 정보
	@Override
	public Optional<Ubook> selectUbookDetailByProductNo(Long productNo) {
	
		String query = "select u " +
						"from Ubook u " +
						"join fetch u.product p " +
						"join fetch p.category c " +
						"left join fetch c.pCategoryId pc " +
						"where p.productNo = :productNo " +
						"and p.productDelFl = 'N' " +
						"and p.productType.typeCode = 3" ;
						
		List<Ubook> ubook = em.createQuery(query, Ubook.class)
							.setParameter("productNo", productNo)
							.getResultList();
		
		if (ubook.isEmpty()) return Optional.empty();
		
		return Optional.of(ubook.get(0));
	}


	// 중고도서 상세 작가 조회
	@Override
	public List<String> selectWritersByProductNo(Long productNo) {
		
		String query = "select pt.tagName " +
						"from ProductTagConnect ptc " +
						"join ptc.product p " +
						"join ptc.productTag pt " +
						"join pt.tagCode tc " +
						"where p.productNo = :productNo " +
						"and tc.tagCode = 1 " +
						"and p.productType.typeCode = 3 " ;
		
		
		
		
		return em.createQuery(query, String.class)
				.setParameter("productNo", productNo)
				.getResultList();
				
				
	}


	// 중고도서 상세 출판사 조회
	@Override
	public List<String> selectPublishersByProductNo(Long productNo) {
		
		String query = "select tagName " +
						"from ProductTagConnect pct " +
						"join pct.product p " +
						"join pct.productTag pt " +
						"join pt.tagCode tc " +
						"where p.productNo = :productNo " +
						"and tc.tagCode = 3 " +
						"and p.productType.typeCode = 3 ";
		
		
		return em.createQuery(query, String.class)
				.setParameter("productNo", productNo)
				.getResultList();
				
	}
	

}
