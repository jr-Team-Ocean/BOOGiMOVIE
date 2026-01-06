package com.bm.project.repository;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.bm.project.entity.Product;

@Repository
@RequiredArgsConstructor
public class MyPageRepositoryImpl implements MyPageRepository {

    private final EntityManager em;

    @Override
    public Page<Product> findByMemberNo(Long memberNo, String order, Pageable pageable) {
        
        // 1. 정렬 조건 설정
        String sortQuery = switch (order) {
            case "price" -> "ORDER BY l.product.productPrice ASC";
            case "name"  -> "ORDER BY l.product.productTitle ASC";
            default      -> "ORDER BY l.product.productNo DESC";
        };

        // 2. 기본 데이터 조회 쿼리
        String jpql = "SELECT l.product FROM Likes l WHERE l.member.memberNo = :memberNo " + sortQuery;
        
        TypedQuery<Product> query = em.createQuery(jpql, Product.class)
                .setParameter("memberNo", memberNo)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        List<Product> content = query.getResultList();

        // 3. 각 Product 객체에 '작가' 정보 채워넣기 (ERD 기반 1번 코드 사용)
     // MyPageRepositoryImpl.java 의 반복문 내부 수정
        for (Product p : content) {
            // Native Query 사용 (엔티티 필드명이 아닌 실제 DB 테이블/컬럼명 기준)
            String nativeSql = "SELECT T.TAG_NAME " +
                               "FROM PRODUCT_TAG_CONNECT PTC " +
                               "JOIN PRODUCT_TAG T ON PTC.TAG_NO = T.TAG_NO " +
                               "WHERE PTC.PRODUCT_NO = :pNo " +
                               "AND T.TAG_CODE = 1";

            try {
                List<String> results = em.createNativeQuery(nativeSql, String.class)
                                         .setParameter("pNo", p.getProductNo())
                                         .setMaxResults(1)
                                         .getResultList();

                if (!results.isEmpty()) {
                    p.setAuthorName(results.get(0));
                } else {
                    p.setAuthorName("저자 미상");
                }
            } catch (Exception e) {
                p.setAuthorName("저자 미상");
            }
        }

        // 4. 전체 개수 조회
        String countJpql = "SELECT COUNT(l) FROM Likes l WHERE l.member.memberNo = :memberNo";
        Long total = em.createQuery(countJpql, Long.class)
                .setParameter("memberNo", memberNo)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public void deleteFavorite(int productNo, Long memberNo) {
        // 1. "Like" -> "Likes"로 변경 (프로젝트의 엔티티 클래스명에 맞춰야 함)
        // 2. l.product.productNo 필드명이 실제 Product 엔티티와 맞는지 확인
        em.createQuery("DELETE FROM Likes l WHERE l.product.productNo = :pNo AND l.member.memberNo = :mNo")
          .setParameter("pNo", (long)productNo) 
          .setParameter("mNo", memberNo) // memberNo는 이미 Long이므로 바로 전달
          .executeUpdate();
    }
}