package com.bm.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bm.project.dto.HomeInterfaceDto;
import com.bm.project.entity.Product;

public interface HomeRepository extends JpaRepository<Product, Long> {
 
	// 인기 도서 조회
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
	List<HomeInterfaceDto> findTop5Products(@Param("typeCode") Long typeCode,
									@Param("creatorCode") Long creatorCode,
									@Param("companyCode") Long companyCode);

}
