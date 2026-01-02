package com.bm.project.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


public class HomeDto {

	// PRODUCT_NO, IMG_PATH, PRODUCT_TITLE, TYPE_CODE, TAG_NAME, TAG_CATEGORY
	// 도서 / 영화 좋아요 많은 순으로 Top5 조회
	@Getter
	@Builder
	@ToString
	public static class HomeLike {
		private Long productNo; // 상품 번호
		private String productTitle; // 도서 또는 영화명
		private String imgPath; // 대표 이미지
		private Long typeCode; // 도서:1 or 영화:2 구분

		private List<String> creator; // 작가명 or 감독명
		private List<String> company; // 출판사 or 제작사
		
		private Long likeCount; // 좋아요 수
		
		// 인터페이스 -> DTO
		public static HomeLike convertToLikeDto(HomeLikeDto raw) {
	        return HomeLike.builder()
	                .productNo(raw.getProductNo())
	                .productTitle(raw.getProductTitle())
	                .imgPath(raw.getImgPath())
	                .likeCount(raw.getLikeCount())
	                // 콤마로 잘라서 리스트로 만들기 (값이 없으면 빈 리스트)
	                .creator(raw.getCreator() != null ? List.of(raw.getCreator().split(", ")) : List.of())
	                .company(raw.getCompany() != null ? List.of(raw.getCompany().split(", ")) : List.of())
	                .build();
	    }
	}
	
	// 도서 + 영화 결제 많은 순으로 Top10 조회
	@Getter
	@Builder
	@ToString
	public static class HomeOrder {
		private Long productNo; // 상품 번호
		private String productTitle; // 도서 또는 영화명
		private String imgPath; // 대표 이미지
		private Long typeCode; // 도서:1 or 영화:2 구분

		private List<String> creator; // 작가명 or 감독명
		private List<String> company; // 출판사 or 제작사
		
		private Long orderCount; // 주문 건수
		
		// 인터페이스 -> DTO
		public static HomeOrder convertToOrderDto(HomeOrderDto raw) {
	        return HomeOrder.builder()
	                .productNo(raw.getProductNo())
	                .productTitle(raw.getProductTitle())
	                .typeCode(raw.getTypeCode())
	                .imgPath(raw.getImgPath())
	                .orderCount(raw.getOrderCount())
	                // 콤마로 잘라서 리스트로 만들기 (값이 없으면 빈 리스트)
	                .creator(raw.getCreator() != null ? List.of(raw.getCreator().split(", ")) : List.of())
	                .company(raw.getCompany() != null ? List.of(raw.getCompany().split(", ")) : List.of())
	                .build();
	    }
	}

}