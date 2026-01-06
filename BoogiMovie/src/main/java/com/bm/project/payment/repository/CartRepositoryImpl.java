package com.bm.project.payment.repository;

import org.springframework.stereotype.Repository;

import com.bm.project.entity.Member;
import com.bm.project.entity.Product;
import com.bm.project.payment.entity.Cart;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class CartRepositoryImpl implements CartRepositoryCustom {

	@PersistenceContext
    private EntityManager em;

	@Override
	public void insertCart(Long memberNo, Long productNo, Integer quantity) {
		Cart cart = Cart.builder()
                        .member(em.getReference(Member.class, memberNo))
                        .product(em.getReference(Product.class, productNo))
                        .quantity(quantity)
                        .build();
		em.persist(cart);
	}
	
	
}
