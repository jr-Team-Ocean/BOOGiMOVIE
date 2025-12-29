package com.bm.project.payment.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.bm.project.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert // INSERT 시 작성한 값만 SQL에 포함, 나머지 default 활용
@DynamicUpdate // UPDATE 시 변경된 필드만 SQL에 포함
public class Delivery {
	// 배송 엔티티
	
	// ==================================================
	
	// 주문 번호 (식별자)
	@Id
	@Column(name = "ORDER_NO", length = 20, nullable = false)
	private String orderNo;
	
	// 식별 관계
	@MapsId
	@OneToOne
	@JoinColumn(name = "ORDER_NO")
	private Orders orders; 
	
	// ==================================================
	
	@Column(name = "RECIPIENT_NAME", length = 30, nullable = false)
	private String recipientName; // 수령인 이름
	
	@Column(name = "RECIPIENT_TEL", length = 11, nullable = false)
	private String recipientTel; // 수령인 연락처
	
	@Column(name = "ORDER_REQUEST", length = 300)
	private String orderRequest; // 요청사항
	
	@Column(name = "POST_CODE", length = 6, nullable = false)
	private String postCode; // 우편번호
	
	@Column(name = "LOAD_ADDRESS", length = 100, nullable = false)
	private String roadAddress; // 도로명 주소
	
	@Column(name = "DETAIL_ADDRESS", length = 100, nullable = false)
	private String detailAddress; // 상세 주소
	
	
	
	

}
