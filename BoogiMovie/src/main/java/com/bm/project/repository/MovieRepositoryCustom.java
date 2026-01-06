package com.bm.project.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bm.project.entity.Movie;
import com.bm.project.entity.Product;
import com.bm.project.entity.ProductTag;
import com.bm.project.entity.Review;
import com.bm.project.entity.TagCode;

public interface MovieRepositoryCustom{

	// 영화 목록 조회
	Page<Movie> selectMovieList(Map<String, Object> paramMap, Pageable pageable);

	// 영화 검색 조회
	Page<Movie> searchMovieList(Map<String, Object> paramMap, Pageable pageable);
	
	boolean existsProductTagConnect(Long productNo, Long tagNo);

	void saveProductTagConnect(Product product, ProductTag tag);

	TagCode getTagCodeRef(long tagCode);
	
	// 좋아요 insert
	int insertLike(Long productNo, Long memberNo);
	
	// 후기 등록
	int insertReview(Long productNo, Long memberNo, Integer reviewScore, String reviewContent);
	
	// 후기 목록
	List<Review> selectReviewList(Long productNo);
	
}
