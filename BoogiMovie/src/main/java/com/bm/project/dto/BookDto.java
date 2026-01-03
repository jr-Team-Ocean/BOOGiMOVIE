package com.bm.project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bm.project.entity.Book;
import com.bm.project.entity.Category;
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
        private String categoryName;
        
        private Long pCategoryId;
        private String pCategoryName;
        
        
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
        
        // 도서 상세 조회용 DTO
        public static Response toDetailDto(
        		Product product,
        		Book book,
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
        				   
        				   .isbn(book.getIsbn())
        				   .writers(writers)
        				   .publishers(publishers)
        				   
        			
        				   .build();
        }
        
        
	}
	
	
	// 등록용
	@Builder
	@Getter
	@Setter
	public static class Create {
		private String isbn;
		private String productTitle;
		private MultipartFile bookImage;
		private String  writers;
		private String publishers;
		private LocalDate productDate;
		private Integer productPrice;
		private Long categoryId;
		private Integer bookCount;
		private String productContent;
		
		
		// 상품부분 담기
		public Product toEntity() {
			return Product.builder()
						  .productTitle(this.productTitle) // this 없어도 되지만 헷갈리지 않게
						  .productContent(this.productContent)
						  .productDate(this.productDate.atStartOfDay())
						  // atStartOfDay() : LocalDate → LocalDateTime 변환 표준 방식
						  .productPrice(productPrice)
						  .imgPath(null)
						  .build();
		}
		
		// 도서부분 담기
		public Book toBookEntity(Product product) {
			return Book.builder()
					   .product(product)
					   .isbn(isbn)
					   .bookCount(bookCount)
					   .build();
			
		}
		
	}
	
	
}
