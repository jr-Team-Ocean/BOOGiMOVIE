package com.bm.project.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bm.project.dto.BookDto;
import com.bm.project.dto.BookDto.Create;
import com.bm.project.dto.BookDto.Update;
import com.bm.project.entity.Review;

public interface BookService {

	// 도서 목록 조회
	Page<BookDto.Response> selectBookList(Map<String, Object> paramMap, Pageable pageable);

	// 검색용 도서 목록 조회
	Page<BookDto.Response> searchBookList(Map<String, Object> paramMap, Pageable pageable);

	// 도서 상세 조회
	BookDto.Response selectBookDetail(Long productNo);

	// 도서 등록 (상품 번호 반환)
	Long bookWrite(Create bookCreate) throws IllegalStateException, IOException;

	// 도서 수정
	void bookUpdate(Long productNo, Update bookUpdate) throws IllegalStateException, IOException;

	// 삭제
	void bookDelete(Long productNo);
	
	// 기존 좋아요 여부
	int bookLikeCheck(Long productNo, Long memberNo);
	
	// 좋아요 처리
	int bookLike(Map<String, Long> paramMap);

	// 좋아요 개수 확인
	int bookLikeCount(Long productNo);

	// 기존 리뷰 목록 조회
	List<Review> selectReviewList(Long productNo);

	// 후기 작성
	int writeReview(Long productNo, Long memberNo, Integer reviewScore, String reviewContent);

	// 후기 수정
	int updateReview(Long reviewNo, Long memberNo, String reviewContent);
	
	// 후기 삭제
	int deleteReview(Long reviewNo, Long memberNo);

	// 평점
	double getReviewAverage(Long productNo);

	// api 임시
	boolean bookWriteByApiIsbn(String isbn);

	

}
