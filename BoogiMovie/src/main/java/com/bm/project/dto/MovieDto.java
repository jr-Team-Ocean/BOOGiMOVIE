package com.bm.project.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bm.project.entity.Movie;
import com.bm.project.entity.Product;
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
		
		private Long tagCode; // 상품 종류(영화)
		
		private Long productNo; // 상품 번호
		private String productTitle; // 영화 제목
		private String productContent; // 영화 소개
		private LocalDateTime productDate; // 영화 개봉일
		private Integer productPrice; // 판매가
		private String imgPath; // 대표이미지
		
		private Integer movieTime; // 상영시간
		private MovieRating filmRating; // 관람등급
		
		private List<String> director; // 감독
		private List<String> company; // 제작사
		private String nation; // 국가
		private List<String> actor; // 출연배우
		
		private Long categoryId;
		private String categoryName;
		
		
		// 영화 목록 조회용 
		public static Response toListDto(Movie movie) {
			Product product = movie.getProduct();
			
			return Response.builder()
					.productNo(product.getProductNo())
					.productTitle(product.getProductTitle())
					.productPrice(product.getProductPrice())
					.productDate(product.getProductDate())
					.filmRating(movie.getFilmRating())
					.categoryId(product.getCategory().getCategoryId())
					.imgPath(product.getImgPath())
					.build();
					
		}
		
	}
	
	// 영화 등록
	@Getter
	@Setter
	@ToString
	public static class Create{
		
		private String movieTitle;
		private String movieContent;
		private MultipartFile imgFile;
		private String imgPath;
		private List<String> director;
		private String nation;
		private List<String> company;
		private List<String> actor;
		private Integer productPrice;
		private Integer movieTime; 
		private MovieRating filmRating;
		private LocalDateTime productDate;
		
		public Movie toEntity(Product product) {
			return Movie.builder()
					.product(product)
					.movieTime(this.movieTime)
					.filmRating(this.filmRating)
					.build();
		}
		
		public Product toProductEntity() {
			return Product.builder()
					.productTitle(this.movieTitle)
					.productContent(this.movieContent)
					.productDate(this.productDate)
					.productPrice(this.productPrice)
					.imgPath(this.imgPath)
					.build();
		}
		
	}
}
