package com.bm.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.bm.project.entity.Product;

public interface MyPageRepository {
   
    Page<Product> findByMemberNo(Long memberNo, String order, Pageable pageable);

	void deleteFavorite(int productNo, Long memberNo);
}