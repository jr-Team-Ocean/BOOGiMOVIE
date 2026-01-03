package com.bm.project.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.bm.project.entity.Book;
import com.bm.project.entity.Category;
import com.bm.project.entity.Product;
import com.bm.project.entity.ProductTag;
import com.bm.project.entity.ProductTagConnect;
import com.bm.project.entity.ProductType;
import com.bm.project.entity.TagCode;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class BookRepositoryImpl implements BookRepository{
	
	@PersistenceContext
	private EntityManager em;

	// 도서 목록 조회
	@Override
	public Page<Product> selectBookList(Map<String, Object> paramMap, Pageable pageable) {
		
		Long categoryId = null;
		String sort = "latest"; // 기본값
		
		// 장르 여부 확인
		if (paramMap.get("category") != null) {
		    categoryId = Long.valueOf(paramMap.get("category").toString());
		}
		
		// 정렬선택 확인
		if (paramMap.get("sort") != null) {
	        sort = paramMap.get("sort").toString();
	    }
		
		String query = "select p " +
				       "from Book b " +
				       "join b.product p " +
					   "where p.productDelFl = 'N' " +
					   "and p.productType.typeCode = 1 " +
					   "and b.bookCount > 0";
		
		
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
		switch (sort) {
	        case "title": query += " order by p.productTitle asc";
	        break;
	        
	        case "latest": query += " order by p.productDate desc";
	        break;
	        
	        default: query += " order by p.productDate desc";
            break;
		}
		
		
		
		
		// 조건 없을 경우
		var bQuery = em.createQuery(query, Product.class);
		
		// 카테고리 선택시 
		if (categoryId != null && categoryId != 0) {
			bQuery.setParameter("categoryId", categoryId);
		}
		
		List<Product> books = bQuery.setFirstResult((int)pageable.getOffset())
				                    // 어디서부터 가지고 올 것인지
									.setMaxResults(pageable.getPageSize())
				                    // 몇개를 가지고 올 것인지
									.getResultList();
		
		
		// 게시글 수
		String countQuery = "select count(p) " +
							"from Book b " +
							"join b.product p " +
		                    "where p.productDelFl = 'N' " +
		                    "and p.productType.typeCode = 1 " +
		                    "and b.bookCount > 0";
		
		// 카테고리 선택시 
		if (categoryId != null && categoryId != 0) {
			
			if (categoryId == 11) {
				countQuery += " and (p.category.categoryId = :categoryId "
						    + "or p.category.pCategoryId.categoryId = :categoryId)";
			} else {				
				countQuery += " and p.category.categoryId = :categoryId";
			}
	    }

		var cQuery = em.createQuery(countQuery, Long.class);
		
		if (categoryId != null && categoryId != 0) {
			cQuery.setParameter("categoryId", categoryId);
        }
		
		Long total = cQuery.getSingleResult();
		                   // 결과 1개 반환
		
		
		return new PageImpl<>(books, pageable, total);
	}
	
	
	
	// 상품번호로 저자목록 조회
	@Override
	public List<Object[]> selectWritersByProductNos(List<Long> productNos) {
		
		// 없을 경우
		if (productNos.isEmpty()) {
			return List.of();
		}
		
		
		String query = "select p.productNo, t.tagName " +
		               "from ProductTagConnect ptc " +
		               "join ptc.product p " +
		               "join ptc.productTag t " +
		               "join t.tagCode tc " +
		               "where p.productNo in :productNos " +
		               "and tc.tagCode = 1";
		
		List<Object[]> result = em.createQuery(query, Object[].class)
		                          .setParameter("productNos", productNos)
		                          .getResultList();
		
		return result;
	}


	// 도서 검색 조회
	@Override
	public Page<Product> searchBookList(Map<String, Object> paramMap, Pageable pageable) {
		
		Long categoryId = null;
		String sort = "latest"; // 기본값
		String keyword = null;
		
		// 장르 여부 확인
		if (paramMap.get("category") != null) {
		    categoryId = Long.valueOf(paramMap.get("category").toString());
		}
		
		// 정렬선택 확인
		if (paramMap.get("sort") != null) {
	        sort = paramMap.get("sort").toString();
	    }
		
		// 검색
		if (paramMap.get("query") != null) {
	        keyword = paramMap.get("query").toString();
	    }
		
		String query = "select p " +
				       "from Book b " +
				       "join b.product p " +
					   "where p.productDelFl = 'N' " +
					   "and p.productType.typeCode = 1 " +
					   "and b.bookCount > 0";
		
		
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
		
		if (keyword != null && !keyword.isBlank()) {
	        query += " and (p.productTitle like :keyword " +
	        		 "or exists (" +
	        		 " select 1 from ProductTagConnect ptc" +
	        		 " join ptc.productTag t" +
	        		 " join t.tagCode tc" +
	        		 " where ptc.product = p" +
	        		 " and tc.tagCode = 1" +
	        		 " and t.tagName like :keyword))";
	    }
		
		// 정렬
		switch (sort) {
	        case "title": query += " order by p.productTitle asc";
	        break;
	        
	        case "latest": query += " order by p.productDate desc";
	        break;
	        
	        default: query += " order by p.productDate desc";
            break;
		}
		
		
		
		
		// 조건 없을 경우
		var bQuery = em.createQuery(query, Product.class);
		
		// 카테고리 선택시 
		if (categoryId != null && categoryId != 0) {
			bQuery.setParameter("categoryId", categoryId);
		}
		
		if (keyword != null && !keyword.isBlank()) {
	        bQuery.setParameter("keyword", "%" + keyword + "%");
	    }
		
		List<Product> books = bQuery.setFirstResult((int)pageable.getOffset())
				                    // 어디서부터 가지고 올 것인지
									.setMaxResults(pageable.getPageSize())
				                    // 몇개를 가지고 올 것인지
									.getResultList();
		
		
		// 게시글 수
		String countQuery = "select count(p) " +
							"from Book b " +
							"join b.product p " +
		                    "where p.productDelFl = 'N' " +
		                    "and p.productType.typeCode = 1 " +
		                    "and b.bookCount > 0";
		
		// 카테고리 선택시 
		if (categoryId != null && categoryId != 0) {
			
			if (categoryId == 11) {
				countQuery += " and (p.category.categoryId = :categoryId "
						    + "or p.category.pCategoryId.categoryId = :categoryId)";
			} else {				
				countQuery += " and p.category.categoryId = :categoryId ";
			}
	    }
		
		if (keyword != null && !keyword.isBlank()) {
	        countQuery += " and (p.productTitle like :keyword " +
	                	  "or exists (" +
		                  " select 1 from ProductTagConnect ptc" +
		                  " join ptc.productTag t" +
		                  " join t.tagCode tc" +
		                  " where ptc.product = p" +
		                  " and tc.tagCode = 1" +
		                  " and t.tagName like :keyword))";
	    }

		var cQuery = em.createQuery(countQuery, Long.class);
		
		if (categoryId != null && categoryId != 0) {
			cQuery.setParameter("categoryId", categoryId);
        }
		
		if (keyword != null && !keyword.isBlank()) {
	        cQuery.setParameter("keyword", "%" + keyword + "%");
	    }
		
		Long total = cQuery.getSingleResult();
		                   // 결과 1개 반환
		
		
		return new PageImpl<>(books, pageable, total);
	}



	// 도서 상세 정보 조회
	@Override
	public Optional<Book> selectBookDetailByProductNo(Long productNo) {
		
		String query = "select b " +
					   "from Book b " +
					   "join fetch b.product p " +
					   "join fetch p.category c " +
					   "left join fetch c.pCategoryId pc " +
					   "where p.productNo = :productNo " +
					   "and p.productDelFl = 'N' " +
					   "and p.productType.typeCode = 1 " +
					   "and b.bookCount > 0";
		
		List<Book> book = em.createQuery(query, Book.class)
                		    .setParameter("productNo", productNo)
                            .getResultList();
		
		if (book.isEmpty()) return Optional.empty();

		return Optional.of(book.get(0));
	}



	// 도서 상세정보 저자 조회
	@Override
	public List<String> selectWritersByProductNo(Long productNo) {

		String query = "select t.tagName " +
					   "from ProductTagConnect ptc " +
					   "join ptc.product p " +
					   "join ptc.productTag t " +
					   "join t.tagCode tc " +
					   "where p.productNo = :productNo " +
					   "and tc.tagCode = 1";
		
		
		return em.createQuery(query, String.class)
	             .setParameter("productNo", productNo)
	             .getResultList();
	}


	// 도서 상세정보 출판사 조회
	@Override
	public List<String> selectPublishersByProductNo(Long productNo) {
		String query = "select t.tagName " +
				   "from ProductTagConnect ptc " +
				   "join ptc.product p " +
				   "join ptc.productTag t " +
				   "join t.tagCode tc " +
				   "where p.productNo in :productNo " +
				   "and tc.tagCode = 3";
	
	
		return em.createQuery(query, String.class)
				 .setParameter("productNo", productNo)
				 .getResultList();
	}



	@Override
	public Category getReference(Class<Category> categoryEntityClass, Long categoryId) {
		return em.getReference(categoryEntityClass, categoryId);
	}

	@Override
	public ProductType getReference(Class<ProductType> pTypeEntityClass, long typeCode) {
		return em.getReference(pTypeEntityClass, typeCode);
	}

	@Override
	public TagCode getTagCodeRef(long code) {
		return em.getReference(TagCode.class, code);
	}

	
	@Override
	public void saveProductTagConnect(Product product, ProductTag tag) {
		
		ProductTagConnect con = ProductTagConnect.builder()
												 .product(product)
												 .productTag(tag)
												 .build();
		em.persist(con);
	}
	
	
}
