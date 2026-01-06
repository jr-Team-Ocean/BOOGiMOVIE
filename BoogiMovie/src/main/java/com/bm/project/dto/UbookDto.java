package com.bm.project.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.bm.project.dto.BookDto.Response;
import com.bm.project.entity.Book;
import com.bm.project.entity.Category;
import com.bm.project.entity.Product;
import com.bm.project.entity.Ubook;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class UbookDto {
	
	@Builder
	@Getter
	@Setter
	public static class Response {
		
		// 상품
		private Long typeCode; // 상품 종류
		
		private Long productNo; // 상품 번호
		private String productTitle; // 도서명
        private String productContent; // 책 소개
        private LocalDateTime productDate; // 출간일
        private Integer productPrice; // 판매가
        private String imgPath; // 대표 이미지

        
        private String ubookStatus; // 중고도서 분류        
        private Long nbookPrice; // 도서 정가
        private String ubookIndex;
        private String authorIntro;
        
        private Long categoryId; // 카테고리
        private String categoryName;
        
        private Long pCategoryId; // 카테고리
        private String pCategoryName;
        
        private List<String> writers; // 작가
        private List<String> publishers; // 출판사
        
        

	
	
        // 게시글 목록 조회용 DTO
        public static Response toUListDto(Product product) {
        	
        	return Response .builder()
        					.productNo(product.getProductNo())
        					.productTitle(product.getProductTitle())
        					.imgPath(product.getImgPath())
        					.productPrice(product.getProductPrice())
        					
        					
        					.build();
        }
        
        
        
        // 도서 상세 조회용 DTO
        public static Response toUbookDetailDto(
        		Product product,
        		Ubook ubook,
        		Category category,
        		Category pCategory,
        		List<String> writers, 
        		List<String> publishers
        		) {
        	
        	return Response.builder()
        				   .productNo(product.getProductNo())
        				   .productTitle(product.getProductTitle())
        				   .productPrice(product.getProductPrice())
        				   .productDate(product.getProductDate())
        				   .productContent(product.getProductContent())
        				   .imgPath(product.getImgPath())
        				   
        				   .categoryId(category.getCategoryId())
        				   .categoryName(category.getCategoryName())
        				   .pCategoryId(pCategory != null ? pCategory.getCategoryId() : null)
        				   .pCategoryName(pCategory != null ? pCategory.getCategoryName() : null)
        				   
        				   .ubookStatus(ubook.getUbookStatus())
        				   .nbookPrice(ubook.getNbookPrice())
        				   .ubookIndex(ubook.getUbookIndex())
        				   .authorIntro(ubook.getAuthorIntro())
        				   
        				   .writers(writers)
        				   .publishers(publishers)
        				   
        			
        				   .build();
        	
        	
        }
	
	}

}
