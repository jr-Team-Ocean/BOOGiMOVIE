package com.bm.project.payment.model.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PayValidationDto {
	
	private Long memberNo; // 주문하려는 회원 번호
	
	private List<OrderItemDto> items; // 주문하는 여러 개의 아이템
	
	@Data
	@NoArgsConstructor
	public static class OrderItemDto {
		private Long productNo; // 상품 번호
		private Integer quantity; // 주문 수량
	}

}
