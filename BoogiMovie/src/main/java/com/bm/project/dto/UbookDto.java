package com.bm.project.dto;

import java.time.LocalDateTime;
import java.util.List;

public class UbookDto {
	
	
	public static class Response {
		
		// 상품
		private Long typeCode; // 상품 종류
		
		private Long productNo; // 상품 번호
		private String productTitle; // 도서명
        private String productContent; // 책 소개
        private LocalDateTime productDate; // 출간일
        private Integer productPrice; // 판매가
        private String imgPath; // 대표 이미지

        
        private UbookStatus ubookStatus; // 중고도서 분류        
        private Long nbookPrice; // 도서 정가
        private String ubookIndex;
        private String authorIntro;
        
        
        private Long categoryId; // 카테고리
        
        private List<String> writers; // 작가
        private List<String> publishers; // 출판사
	
	
        // 게시글 상세 조회용 DTO
        public static Response toDto()
	
	}

}
