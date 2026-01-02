package com.bm.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bm.project.dto.HomeLikeDto;
import com.bm.project.dto.HomeOrderDto;
import com.bm.project.entity.Product;

public interface HomeRepository extends JpaRepository<Product, Long> {
	
	// 결제가 가장 많은 도서 및 영화 조회
	@Query(value = 
			"SELECT " +
				"	p.PRODUCT_NO AS productNo, " +
				"	p.PRODUCT_TITLE AS productTitle, " +
				"	p.IMG_PATH AS imgPath, " +
		        // 창작자 (작가/감독)
		        "    (SELECT LISTAGG(t.TAG_NAME, ', ') WITHIN GROUP (ORDER BY t.TAG_NAME) " +
		        "     FROM PRODUCT_TAG t " +
		        "     JOIN PRODUCT_TAG_CONNECT c ON t.TAG_NO = c.TAG_NO " +
		        "     WHERE c.PRODUCT_NO = p.PRODUCT_NO " +
		        "	  AND t.TAG_CODE IN (1, 2)) AS creator, " +
		        // 회사 (출판사/제작사)
		        "    (SELECT LISTAGG(t.TAG_NAME, ', ') WITHIN GROUP (ORDER BY t.TAG_NAME) " +
		        "     FROM PRODUCT_TAG t " +
		        "     JOIN PRODUCT_TAG_CONNECT c ON t.TAG_NO = c.TAG_NO " +
		        "     WHERE c.PRODUCT_NO = p.PRODUCT_NO  " +
		        "	  AND t.TAG_CODE IN (3, 4)) AS company, " +
		        // 주문건수
		        "    (SELECT COUNT(od.ORDER_NO) " +
		        "     FROM ORDERS_DETAIL od " +
		        "     JOIN ORDERS o ON od.ORDER_NO = o.ORDER_NO " +
		        "     WHERE od.PRODUCT_NO = p.PRODUCT_NO " +
		        "     AND o.PAY_STATUS = 'PAID') AS orderCount, " +
		        // 도서 또는 영화 구분 TYPE 번호
		        "	 p.TYPE_CODE AS typeCode " +
	        "FROM PRODUCT p " +
	        "WHERE p.PRODUCT_DEL_FL = 'N' " +
	        "ORDER BY orderCount DESC, productNo DESC " +
	        "FETCH FIRST 10 ROWS ONLY",
	        nativeQuery = true)
	List<HomeOrderDto> getPopularProducts();
	
 
	// 인기 도서 및 영화 조회 (좋아요 개수 기준)
	@Query(value =
	        "SELECT " +
	        "    p.PRODUCT_NO AS productNo, " +
	        "    p.PRODUCT_TITLE AS productTitle, " +
	        "    p.IMG_PATH AS imgPath, " +
	        "    (SELECT COUNT(*) FROM LIKES l WHERE l.PRODUCT_NO = p.PRODUCT_NO) AS likeCount, " +
	        // 창작자 (작가/감독)
	        "    (SELECT LISTAGG(t.TAG_NAME, ', ') WITHIN GROUP (ORDER BY t.TAG_NAME) " +
	        "     FROM PRODUCT_TAG t " +
	        "     JOIN PRODUCT_TAG_CONNECT c ON t.TAG_NO = c.TAG_NO " +
	        "     WHERE c.PRODUCT_NO = p.PRODUCT_NO AND t.TAG_CODE = :creatorCode) AS creator, " +
	        // 회사 (출판사/제작사)
	        "    (SELECT LISTAGG(t.TAG_NAME, ', ') WITHIN GROUP (ORDER BY t.TAG_NAME) " +
	        "     FROM PRODUCT_TAG t " +
	        "     JOIN PRODUCT_TAG_CONNECT c ON t.TAG_NO = c.TAG_NO " +
	        "     WHERE c.PRODUCT_NO = p.PRODUCT_NO AND t.TAG_CODE = :companyCode) AS company " +
	        "FROM PRODUCT p " +
	        "WHERE p.TYPE_CODE = :typeCode " +
	        "AND p.PRODUCT_DEL_FL = 'N' " +
	        "ORDER BY likeCount DESC, p.PRODUCT_NO DESC " +
	        "FETCH FIRST 5 ROWS ONLY", 
	        nativeQuery = true)
	List<HomeLikeDto> findTop5Products(@Param("typeCode") Long typeCode,
									@Param("creatorCode") Long creatorCode,
									@Param("companyCode") Long companyCode);


}
