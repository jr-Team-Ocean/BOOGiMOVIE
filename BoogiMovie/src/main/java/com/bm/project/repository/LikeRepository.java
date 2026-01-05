package com.bm.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bm.project.entity.Likes;
import com.bm.project.entity.LikesId;

public interface LikeRepository extends JpaRepository<Likes, LikesId>{
	
	// 기존 좋아요 여부 확인
	boolean existsByProduct_ProductNoAndMember_MemberNo(Long productNo, Long memberNo);
	
	// 삭제 처리
	void deleteByProduct_ProductNoAndMember_MemberNo(Long productNo, Long memberNo);

	
	// 좋아요 수
	int countByProduct_ProductNo(Long productNo);



}
