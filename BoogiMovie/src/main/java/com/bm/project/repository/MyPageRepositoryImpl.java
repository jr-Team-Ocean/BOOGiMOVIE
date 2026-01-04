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
        
        // 1. 정렬 조건 설정 (Likes 엔티티를 거쳐 Product 필드에 접근)
        String sortQuery = switch (order) {
            case "price" -> "ORDER BY l.product.productPrice ASC";
            case "name"  -> "ORDER BY l.product.productTitle ASC";
            default      -> "ORDER BY l.product.productNo DESC"; // 기본 최신순
        };

        // 2. 데이터 조회 쿼리 (Likes 테이블에서 해당 회원의 상품만 추출)
        // l.member.memberNo는 Likes 엔티티 안의 member 필드의 memberNo를 의미합니다.
        String jpql = "SELECT l.product FROM Likes l " +
                     "WHERE l.member.memberNo = :memberNo " + sortQuery;
        
        TypedQuery<Product> query = em.createQuery(jpql, Product.class)
                .setParameter("memberNo", memberNo)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        List<Product> content = query.getResultList();

        // 3. 전체 개수 조회 쿼리
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