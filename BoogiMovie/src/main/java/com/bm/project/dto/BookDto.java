package com.bm.project.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.bm.project.entity.Product;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class BookDto {
	
	@Builder
	@Getter
	@Setter
	public static class Response {
		
		// 상품
		private Long typeNo; // 상품 종류
		
		private Long productNo; // 상품 번호
		private String productTitle; // 도서명
        private String productContent; // 책 소개
        private LocalDateTime productDate; // 출간일
        private Integer productPrice; // 판매가
        private String imgPath; // 대표 이미지

        
        private Integer bookCount; // 재고
        private String isbn; // isbn
        
        private Long categoryId; // 카테고리
        
        private List<String> writers; // 작가
        private List<String> publishers; // 출판사
        
        
        // 도서 목록 조회용 DTO
        public static Response toListDto(Product product, List<String> writers) {
        	
        	return Response .builder()
        					.productNo(product.getProductNo())
        					.productTitle(product.getProductTitle())
        					.imgPath(product.getImgPath())
        					.productPrice(product.getProductPrice())
        					.writers(writers)
        					.categoryId(product.getCategory().getCategoryId())
        					
        					.build();
        }
        
        
	}
	
}
