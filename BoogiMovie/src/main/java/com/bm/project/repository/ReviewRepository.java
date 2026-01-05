package com.bm.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bm.project.entity.Review;

public interface ReviewRepository  extends JpaRepository<Review, Long> {

	List<Review> findByProductNoOrderByReviewTimeDesc(Long productNo); 
	
}
