package com.bm.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bm.project.entity.Book;
import com.bm.project.entity.Review;

public interface ReviewRepository  extends JpaRepository<Review, Long> {
	
	// 후기 수정
	Optional<Review> findByReviewNoAndMemberNo(Long reviewNo, Long memberNo);

	// 후기 삭제
	void deleteByReviewNoAndMemberNo(Long reviewNo, Long memberNo);


	
}
