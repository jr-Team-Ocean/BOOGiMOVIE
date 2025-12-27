package com.bm.project.entity.pay;


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class PayStatus {
	// 결제 상태 엔티티
	
	@Id
	@Column(name = "PAY_CODE", nullable = false)
	private Integer payCode;
	
	@Column(name = "STATUS", length = 50, nullable = false)
	private String status; // 결제 상태

}
