package com.bm.project.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.bm.project.enums.CommonEnums.MovieRating;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class MovieDto {

	public static class Response{
		
		private Long tagCode; // 상품 종류(영화)
		
		private String productNo; // 상품 번호
		private String productTitle; // 영화 제목
		private String productContent; // 영화 소개
		private LocalDateTime productDate; // 영화 개봉일
		private Integer productPrice; // 판매가
		private String imgPath; // 대표이미지
		
		private Integer movieTime; // 상영시간
		private MovieRating filmRating; // 관람등급
		
		private List<String> supervision; // 감독
		private List<String> company; // 제작사
		private String nation; // 국가
		private List<String> actor; // 출연배우
		
		
		// 영화 목록 조회
		
	}
	
	// 영화 등록
	@Getter
	@Setter
	@ToString
	public static class Create{
		
		private String movieTitle;
		private String movieContent;
		private String imgPath;
		private List<String> supervision;
		private String nation;
		private List<String> company;
		private List<String> actor;
		private Integer productPrice;
		private Integer movieTime; 
		private MovieRating filmRating;
		private LocalDateTime productDate;
		
	}
}
