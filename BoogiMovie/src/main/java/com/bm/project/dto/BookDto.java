package com.bm.project.dto;

import java.time.LocalDateTime;
import java.util.List;

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
        private List<Long> publishers; // 출판사
        
        
        // 도서 목록 조회
//        public static Response toListDto(Product product) {
        	
//        }
        
        
	}
	
}
