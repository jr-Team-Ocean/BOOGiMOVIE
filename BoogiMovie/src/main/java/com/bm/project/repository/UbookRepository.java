package com.bm.project.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bm.project.entity.Category;
import com.bm.project.entity.Product;
import com.bm.project.entity.ProductTag;
import com.bm.project.entity.ProductType;
import com.bm.project.entity.TagCode;
import com.bm.project.entity.Ubook;

public interface UbookRepository {

	// 중고도서 목록 조회
	Page<Product> selectbookList(Map<String, Object> paramMap, Pageable pageable);

	// 중고도서 상태 조회
	List<Object[]> selectUbookStateList(List<Long> productNos);

	// 중고도서 상세 조회
	Optional<Ubook> selectUbookDetailByProductNo(Long productNo);

	// 중고도서 상세 저자 불러오기
	List<String> selectWritersByProductNo(Long productNo);

	// 중고도서 상세 출판사 불러오기
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



	

}
