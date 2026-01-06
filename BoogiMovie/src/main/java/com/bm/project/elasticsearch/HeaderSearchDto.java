package com.bm.project.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// 통합 검색 결과 담을 DTO
@Getter
@Setter
public class HeaderSearchDto {
	private List<SearchItemDto> books = new ArrayList<>();
	private List<SearchItemDto> movies = new ArrayList<>();

	@Getter
	@Builder
	public static class SearchItemDto {
		private Long productNo; // 상품 번호
		private String productTitle;
		private String imgPath;
		private String categoryName;
		private String productType; // "도서", "영화"
		private String publisher; // 출판사 또는 제작사
		private String creator; // 작가 또는 감독
	}

	public void result(ProductDocument document) {
		// document에서는 작가/감독/배우, 출판사/제작사가 리스트로 들어옴

		// 출판사 또는 제작사 하나로 통합
		String publisherText = (document.getPublisher() != null && !document.getPublisher().isEmpty())
				? String.join(", ", document.getPublisher())
				: "";

		// 도서일 경우 작가, 영화일 경우 감독
		String creatorText = "";
		if ("도서".equals(document.getProductType())) {
			// 도서 -> 작가
			creatorText = (document.getAuthors() != null && !document.getAuthors().isEmpty())
					? String.join(", ", document.getAuthors())
					: "";

		} else if ("영화".equals(document.getProductType())) {
			// 영화 -> 감독
			creatorText = (document.getDirectors() != null && !document.getDirectors().isEmpty())
					? String.join(", ", document.getDirectors())
					: "";
		} // if else if

		SearchItemDto item = SearchItemDto.builder()
				.productNo(document.getProductNo())
				.productTitle(document.getProductTitle())
				.imgPath(document.getImgPath())
				.categoryName(document.getCategoryName())
				.productType(document.getProductType())

				// 분리한 값
				.publisher(publisherText)
				.creator(creatorText)
				.build();

		if ("도서".equals(document.getProductType())) {
			this.books.add(item);
		} else if ("영화".equals(document.getProductType())) {
			this.movies.add(item);
		}
	}
}
