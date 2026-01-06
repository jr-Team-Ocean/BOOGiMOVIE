package com.bm.project.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.bm.project.entity.Member;
import com.bm.project.entity.Product;
import com.bm.project.payment.entity.Cart;
import com.bm.project.payment.model.dto.ICartDto;

public interface CartRepository extends JpaRepository<Cart, Long> {

	// 장바구니 목록 조회
	@Query(value = 
			"SELECT " 
	        + "	   C.CART_NO AS cartNo, P.TYPE_CODE AS typeCode, "
			+ "    P.IMG_PATH AS imgPath, P.PRODUCT_TITLE AS productTitle, "
			+ "    P.PRODUCT_PRICE as productPrice, C.PRODUCT_NO AS productNo, "
			+ "    QUANTITY AS quantity, MEMBER_NO AS memberNo "
			+ "FROM CART C\r\n"
			+ "JOIN PRODUCT P ON C.PRODUCT_NO = P.PRODUCT_NO "
			+ "WHERE MEMBER_NO = :memberNo "
			+ "ORDER BY C.CART_NO DESC", 
			nativeQuery = true)
	List<ICartDto> findCartListByMemberNo(@Param("memberNo") Long memberNo);
	
	// 결제한 아이템 장바구니에서 삭제
	void deleteByMemberAndProduct(Member member, Product product);

	
	Optional<Cart> findByMemberMemberNoAndProductProductNo(Long memberNo, Long productNo);

}
