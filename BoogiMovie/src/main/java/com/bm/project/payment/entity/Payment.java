package com.bm.project.payment.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
public class Payment {
	// 결제 수단 엔티티
	
	@Id
	@Column(name = "PAY_NO", length = 100, nullable = false)
	private String payNo; // 결제 ID (imp_uid)
	
	@Column(name = "PAY_METHOD", length = 50, nullable = false)
	private String payMethod; // 결제 수단
	
	// columnDefinition: JPA가 테이블을 생성 하는 쿼리를 만들 때, 해당 문자열 넣음
	@Column(name = "PAY_DATE", 
			columnDefinition = "DATE DEFAULT SYSDATE",
			nullable = false)
	private LocalDateTime payDate; // 결제 일시
	
	@Column(name = "PAY_PRICE", nullable = false)
	private Integer payPrice; // 결제 금액
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_NO", nullable = false)
    private Orders orders;
	

}
