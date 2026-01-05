package com.bm.project.payment.repository;

public interface CartRepositoryCustom {

	// 장바구니에 추가
	void insertCart(Long memberNo, Long productNo, Integer quantity);
	
}
