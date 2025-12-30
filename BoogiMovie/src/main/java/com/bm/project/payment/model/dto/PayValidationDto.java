package com.bm.project.payment.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.bm.project.payment.entity.Delivery;
import com.bm.project.payment.entity.Orders;
import com.bm.project.payment.entity.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class PayValidationDto {
	
	// 결제 요청 DTO (js -> 스프링)
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	public static class PayRequest {
		private Long memberNo; // 주문하려는 회원 번호
		
		private List<OrderItemDto> orderItems; // 주문하는 여러 개의 아이템
	}
	
	
	// 결제 요청 안에 들어가는 아이템 정보
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	public static class OrderItemDto {
		private Long productNo; // 상품 번호
		private Integer quantity; // 주문 수량
	}
	
	// 응답용 DTO (검증 후 반환할 데이터들)
	@Getter @Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	public static class PayResponse {
		private String orderNo;
		private Integer totalPrice;
		private String orderName;
		private String recipientName;
		private String recipientPhone;
	}
	
	// 결제 진행 후 사후 검증 요청 및 테이블에 데이터 저장
	@Getter @Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	public static class PaySuccessDto {
		private String orderNo; // 검증
		
		private String payNo;
		private String payMethod;
		private Integer payPrice; // 검증, payment 데이터
		
		private String recipientName;
		private String recipientTel;
		private String orderRequest;
		private String postCode;
		private String roadAddress;
		private String detailAddress;
		
	    // DTO -> Entity
	    public Payment toPaymentEntity(Orders orders) {
	    	return Payment.builder()
	    			.payNo(this.payNo)
	    			.payMethod(this.payMethod)
	    			.payPrice(this.payPrice)
	                .payDate(LocalDateTime.now()) // 결제 시각은 현재 시간
	                .orders(orders)
	    			.build();
	    }
	    
	    public Delivery toDeliveryEntity(Orders order) {
	        return Delivery.builder()
	                .orders(order)              // 식별자 관계 설정
	                .recipientName(this.recipientName)
	                .recipientTel(this.recipientTel)
	                .orderRequest(this.orderRequest)
	                .postCode(this.postCode)
	                .roadAddress(this.roadAddress)
	                .detailAddress(this.detailAddress)
	                .build();
	    }
	}

}
