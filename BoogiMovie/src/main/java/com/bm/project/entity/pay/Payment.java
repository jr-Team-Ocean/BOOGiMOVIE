package com.bm.project.entity.pay;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	@Column(name = "PAY_NO", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PAY_NO")
	@SequenceGenerator(name = "SEQ_PAY_NO", 
					   sequenceName = "SEQ_PAY_NO",
					   initialValue = 1,
					   allocationSize = 1)
	private Integer payNo;
	
	@Column(name = "PAY_METHOD", length = 50, nullable = false)
	private String payMethod;
	
	// columnDefinition: JPA가 테이블을 생성 하는 쿼리를 만들 때, 해당 문자열 넣음
	@Column(name = "PAY_DATE", 
			columnDefinition = "DATE DEFAULT SYSDATE",
			nullable = false)
	private LocalDateTime payDate;
	
	@Column(name = "PAY_PRICE", nullable = false)
	private Integer payPrice;
	

}
