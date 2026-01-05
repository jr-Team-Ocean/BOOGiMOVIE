package com.bm.project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

import com.bm.project.entity.Category;
import com.bm.project.entity.Movie;
import com.bm.project.entity.Product;
import com.bm.project.entity.ProductTag;
import com.bm.project.entity.ProductTagConnect;
import com.bm.project.enums.CommonEnums.MovieRating;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class MovieDto {

	@Builder
	@Getter
	public static class Response{
		
		private Long productNo; // 상품 번호
		private String productTitle; // 영화 제목
		private String productContent; // 영화 소개
		private LocalDateTime productDate; // 영화 개봉일
		private Integer productPrice; // 판매가
		private String imgPath; // 대표이미지
		
		private Integer movieTime; // 상영시간
		private MovieRating filmRating; // 관람등급
		private String filmRatingLabel; // 관람등급(한글)
		
		private List<String> directors; // 감독
		private List<String> companies; // 제작사
		private List<String> nation; // 국가
		private List<String> actors; // 출연배우
		
		private Long categoryId;
		private Long parentId;
		private String categoryName;
		private String parentName;
		
		
		// 영화 목록 조회용 
		public static Response toListDto(Movie movie) {
			Product product = movie.getProduct();
			
			return Response.builder()
					.productNo(product.getProductNo())
					.productTitle(product.getProductTitle())
					.productPrice(product.getProductPrice())
					.productDate(product.getProductDate())
					.filmRatingLabel(movie.getFilmRating().getDescripton())
					.categoryId(product.getCategory().getCategoryId())
					.imgPath(product.getImgPath())
					.build();
		}
		
		// 영화 상세 조회용
		public static Response toDto(Movie movie) {
			Product product = movie.getProduct();
			Category category = product.getCategory();
			List<ProductTagConnect> ptcList = product.getProductTagConnects();
			
			return Response.builder()
					.productNo(movie.getProductNo())
					.productTitle(product.getProductTitle())
					.imgPath(product.getImgPath())
					.productContent(product.getProductContent())
					.productDate(product.getProductDate())
					.productPrice(product.getProductPrice())
					
					.movieTime(movie.getMovieTime())
					.filmRating(movie.getFilmRating())
					.filmRatingLabel(movie.getFilmRating().getDescripton())
					.categoryId(category.getCategoryId())
					.parentId(category.getPCategoryId().getCategoryId())
					
					.categoryName(category.getCategoryName())
					.parentName(category.getPCategoryId().getCategoryName())
					
					.actors(getTagsByCode(ptcList, 5))
					.directors(getTagsByCode(ptcList, 2))
					.companies(getTagsByCode(ptcList, 4))
					.nation(getTagsByCode(ptcList, 6))
					.build();
		}
		
	}
	
	// 영화 등록
	@Getter
	@Setter
	@ToString
	public static class Create{
		
		private String productTitle;
		private String productContent;
		private MultipartFile movieImg;
		private String imgPath;
		private String directors;
		private String nation;
		private String companies;
		private String actors;
		private Integer productPrice;
		private Integer movieTime; 
		private MovieRating filmRating;
		private LocalDate productDate;
		private Long categoryId;
		
		// 상품
		public Product toProductEntity() {
			return Product.builder()
					.productTitle(this.productTitle)
					.productContent(this.productContent)
					.productDate(this.productDate.atStartOfDay())
					.productPrice(this.productPrice)
					.imgPath(this.imgPath)
					.build();
		}
		
		// 영화
		public Movie toEntity(Product product) {
			return Movie.builder()
					.product(product)
					.movieTime(this.movieTime)
					.filmRating(this.filmRating)
					.build();
		}
	}
	
	// 영화 수정
	@Getter
	@Setter
	@ToString
	public static class Update{
		private String productTitle;
		private String productContent;
		private MultipartFile movieImg;
		private String imgPath;
		private String directors;
		private String nation;
		private String companies;
		private String actors;
		private Integer productPrice;
		private Integer movieTime; 
		private MovieRating filmRating;
		private LocalDate productDate;
		private Long categoryId;
	}
	
	
	// 상세조회시 리스트로 태그이름 읽어오기
	private static List<String> getTagsByCode(List<ProductTagConnect> ptcList, int targetCode) {
	    if (ptcList == null) {
	        return new ArrayList<>();
	    }

	    return ptcList.stream()
	            // 1. Connect 엔티티에서 실제 Tag 엔티티를 꺼냄
	            .map(ProductTagConnect::getProductTag)
	            
	            // 2. 해당 Tag의 코드가 우리가 찾는 코드(예: 5번 배우)인지 확인
	            .filter(tag -> {
	                // 1. null 체크
	                if (tag == null || tag.getTagCode() == null) return false;
	                
	                // 2. 숫자로 변환하여 비교 (엔티티 구조에 맞춰 호출)
	                // 만약 tag.getTagCode() 자체가 숫자(int)라면 .getTagCode()를 한 번만 쓰세요.
	                Object codeObj = tag.getTagCode();
	                Long code;
	                
	                if (codeObj instanceof Long) {
	                    code = (Long) codeObj;
	                } else {
	                    // TagCode가 엔티티 객체라면 그 안의 ID값을 꺼냄
	                    code = tag.getTagCode().getTagCode(); 
	                }
	                
	                return code == targetCode;
	            })
	            
	            // 3. 조건에 맞으면 태그 이름(TagName)만 추출
	            .map(ProductTag::getTagName)
	            
	            // 4. 리스트로 수집
	            .collect(Collectors.toList());
	}
}
