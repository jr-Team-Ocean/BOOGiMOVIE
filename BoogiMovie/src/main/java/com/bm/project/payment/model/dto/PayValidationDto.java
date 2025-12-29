package com.bm.project.payment.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PayValidationDto {
	
	// 결제 요청 DTO (js -> 스프링)
	@Data
	@NoArgsConstructor
	public static class PayRequest {
		private Long memberNo; // 주문하려는 회원 번호
		
		private List<OrderItemDto> orderItems; // 주문하는 여러 개의 아이템
	}
	
	
	// 결제 요청 안에 들어가는 아이템 정보
	@Data
	@NoArgsConstructor
	public static class OrderItemDto {
		private Long productNo; // 상품 번호
		private Integer quantity; // 주문 수량
	}
	
	// 응답용 DTO (검증 후 반환할 데이터들)
	@Getter @Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PayResponse {
		private String orderNo;
		private Integer totalPrice;
		private String orderName;
		private String recipientName;
		private String recipientPhone;
	}

}
