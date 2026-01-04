package com.bm.project.payment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bm.project.dto.IMyPageDto;
import com.bm.project.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
	
	// 내가 소장한 영화 조회
	@Query(value = 
			"""
				SELECT DISTINCT 
				    P.PRODUCT_NO AS productNo, 
				    P.PRODUCT_TITLE AS productTitle, 
				    P.IMG_PATH as imgPath
				FROM ORDERS O
				JOIN ORDERS_DETAIL D ON O.ORDER_NO = D.ORDER_NO
				JOIN PRODUCT P ON D.PRODUCT_NO = P.PRODUCT_NO
				WHERE O.MEMBER_NO = :memberNo
				  AND P.TYPE_CODE = 2 
				ORDER BY P.PRODUCT_NO DESC
			""", nativeQuery = true)
	List<IMyPageDto> getPurchasedMovie(@Param("memberNo") Long memberNo);

}
