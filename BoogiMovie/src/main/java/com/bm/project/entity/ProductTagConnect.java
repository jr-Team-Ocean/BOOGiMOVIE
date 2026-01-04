package com.bm.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "PRODUCT_TAG_CONNECT")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class ProductTagConnect {
	
	@Id
	@Column(name = "PRODUCT_TAG_NO", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_connect_seq")
	@SequenceGenerator(name = "tag_connect_seq", sequenceName = "seq_tag_connect_no", initialValue = 1, allocationSize = 1)
	private Long productTagNo;
	
	
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_NO", nullable = false)
	private Product product;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TAG_NO", nullable = false)
	private ProductTag productTag;
	
	
	// 연관편의
	public void addProduct(Product product, ProductTag tag) {
		this.product = product;
		this.productTag = tag;
		
		if(!product.getProductTagConnects().contains(this)) {
			// 중복 방지
			product.getProductTagConnects().add(this);
			// 없으면 추가
		}
	}
	
	
	
	
}
