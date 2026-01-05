package com.bm.project.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bm.project.entity.Book;
import com.bm.project.entity.Category;
import com.bm.project.entity.Product;
import com.bm.project.entity.ProductTag;
import com.bm.project.entity.ProductType;
import com.bm.project.entity.TagCode;

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

	// 도서 카테고리 얻어오기
	Category getReference(Class<Category> categoryEntityClass, Long categoryId);

	// 도서 분류 얻어오기
	ProductType getReference(Class<ProductType> pTypeEntityClass, long typeCode);

	// 작가 출판사 번호
	TagCode getTagCodeRef(long code);

	// 상품에 태그 연결
	void saveProductTagConnect(Product product, ProductTag tag);

	// 태그 연결 초기화
	void deleteConnect(Long productNo);

	// 좋아요 추가
	int insertLike(Long productNo, Long memberNo);

}
