package com.bm.project.payment.model.service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bm.project.entity.Member;
import com.bm.project.entity.Product;
import com.bm.project.payment.entity.Orders;
import com.bm.project.payment.entity.OrdersDetail;
import com.bm.project.payment.model.dto.PayValidationDto;
import com.bm.project.payment.model.dto.PayValidationDto.PayResponse;
import com.bm.project.payment.repository.OrdersRepository;
import com.bm.project.payment.repository.ProductRepository;
import com.bm.project.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	
	private final OrdersRepository ordersRepo;
	private final MemberRepository memberRepo;
	private final ProductRepository productRepo;
	


	// 결제 전 총금액 사전 검증
	@Override
	@Transactional
	public PayResponse prePaymentValidation(PayValidationDto.PayRequest payValidation) {
		Member member = memberRepo.findById(payValidation.getMemberNo())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

		
		
		// 주문 번호 난수 (20251229-UUID 8자리)
		String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
							+ "-" + UUID.randomUUID().toString().substring(0, 8);
		
		Orders order = Orders.builder()
                .orderNo(orderNo) // 주문번호 (난수)
                .member(member)
                .payStatus("READY") // 결제 대기
                .orderQuantity(0) // 수량 일단 0으로 초기화
                .build();
		
		int totalPrice = 0; // 총 결제 금액
		int totalQuantity = 0; // 총 주문 수량
		String firstProductName = ""; // 첫 상품명 
		
		// OrderDetail 생성 및 가격 합산하기
		for(int i = 0; i < payValidation.getOrderItems().size(); i++) {
			// 주문하는 아이템 하나씩 꺼내오기
			PayValidationDto.OrderItemDto itemDto = payValidation.getOrderItems().get(i);
			
			Product product = productRepo.findById(itemDto.getProductNo())
					.orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
			
			// 첫 번째 상품 이름 저장
			if(i == 0) {
				firstProductName = product.getProductTitle(); // 상품명 (도서: 책 제목, 영화: 영화 제목)
			}
			
			OrdersDetail detail = OrdersDetail.builder()
					.orders(order) 						  // 주문 번호
					.product(product) 					  // 상품 번호
					.itemPrice(product.getProductPrice()) // DB에 저장된 상품 가격
					.itemQuantity(itemDto.getQuantity())  // 주문 수량
					.build();
					
			
			// 연관관계 편의 메소드 (Orders에 Detail 추가)
			order.addOrderDetail(detail);
			
			// 금액 및 수량 누적하기
			totalPrice += (product.getProductPrice() * itemDto.getQuantity());
			totalQuantity += itemDto.getQuantity();
		}
		
		// Orders에 전체 주문 개수 세팅
		order.setOrderQuantity(totalQuantity);
		
		// DB에 저장
		ordersRepo.save(order);
		
		// 주문명 (여러 개면 A 상품 외 2건...)
		String orderName = firstProductName;
		if(payValidation.getOrderItems().size() > 1) {
			// 주문하는 상품이 여러 개라면
			orderName += " 외 " + (payValidation.getOrderItems().size() - 1) + "건";
		}
		
		// 응답 DTO
		return PayValidationDto.PayResponse.builder()
				.orderNo(orderNo) // 주문번호 (난수)
				.totalPrice(totalPrice)
				.orderName(orderName)
				.recipientName(member.getMemberName())
				.recipientPhone(member.getMemberPhone())
				.build();
		
	}



	// 결제 진행 중 취소 또는 실패시
	@Override
	@Transactional // 변경 감지
	public void failPayment(String orderNo, String reason) {
		Orders orders = ordersRepo.findById(orderNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 주문건이 존재하지 않습니다."));
		
		orders.setPayStatus(reason);
	}

}
