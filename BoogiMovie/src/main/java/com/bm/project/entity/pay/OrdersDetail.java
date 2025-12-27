package com.bm.project.entity.pay;


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
public class OrdersDetail {
	
	@Id
	@Column(name = "ORDER_ITEM_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ITEM_NO")
	@SequenceGenerator(name = "SEQ_ITEM_NO", 
					   sequenceName = "SEQ_ITEM_NO",
					   initialValue = 1,
					   allocationSize = 1)
	private Long orderItemId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORDER_NO", nullable = false)
	private Orders orders; // 주문 번호
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_NO")
	private Product product; // 상품 번호
	
	private Integer itemPrice; // 구매 당시 판매가
	
	private Integer itemQuantity; // 주문 상품 개별 수량

}
