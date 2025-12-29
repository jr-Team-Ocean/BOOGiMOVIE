package com.bm.project.payment.model.dto;

import com.bm.project.dto.MemberDto;
import com.bm.project.entity.Member;
import com.bm.project.payment.entity.Delivery;
import com.bm.project.payment.entity.Orders;
import com.bm.project.payment.entity.OrdersDetail;

// 결제 관련
public class PaymentMapper {
	
	// Orders
	// DTO -> Entity
//	public static Orders toEntityOrders(OrderRequestDto orderReq, Member member, String orderNo) {
//		return Orders.builder()
//				.orderNo(orderNo)
//				.member(member)
//				.orderQuantity(orderReq.getItems().size())
//				.payStatus("PAID")
//				.build();
//	}
//	
//	// Delivery
//	// DTO -> Entity
//	public static Delivery toEntityDelivery(OrderRequestDto orderReq, Orders orders) {
//		return Delivery.builder()
//				.orders(orders)
//				.recipientName(orderReq.getRecipientName())
//				.recipientTel(orderReq.getRecipientPhone())
//				.orderRequest(orderReq.getDeliveryRequest())
//				.postCode(orderReq.getPostCode())
//				.roadAddress(orderReq.getRoadAddress())
//				.detailAddress(orderReq.getDetailAddress())
//				.build();
//	}
	
	// OrderDetail
	// DTO -> Entity
//	public static OrdersDetail toEntityOrdersDetail() {
//		return OrdersDetail.builder()
//				;
//	}

}
