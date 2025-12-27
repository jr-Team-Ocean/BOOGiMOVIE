package com.bm.project.entity.pay;


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.bm.project.entity.Member;
import com.bm.project.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert // INSERT 시 작성한 값만 SQL에 포함, 나머지 default 활용
@DynamicUpdate // UPDATE 시 변경된 필드만 SQL에 포함
public class Cart {
	// 장바구니 엔티티

	@Id
	@Column(name = "CART_NO", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CART_NO")
	@SequenceGenerator(name = "SEQ_CART_NO", 
					   sequenceName = "SEQ_CART_NO",
					   initialValue = 1,
					   allocationSize = 1)
	private Long cartNo;
	
	@Column(name = "QUANTITY", nullable = false)
	private Integer quantity;
	
	// Cart: 연관관계 주인
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MEMBER_NO", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_NO", nullable = false)
	private Product product;
	
	
	
	
}
