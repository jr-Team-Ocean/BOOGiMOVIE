package com.bm.project.payment.model.service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bm.project.entity.Member;
import com.bm.project.entity.Product;
import com.bm.project.payment.entity.Delivery;
import com.bm.project.payment.entity.Orders;
import com.bm.project.payment.entity.OrdersDetail;
import com.bm.project.payment.entity.Payment;
import com.bm.project.payment.model.dto.PayValidationDto;
import com.bm.project.payment.model.dto.PayValidationDto.OrderItemDto;
import com.bm.project.payment.model.dto.PayValidationDto.PayResponse;
import com.bm.project.payment.model.dto.PayValidationDto.PaySuccessDto;
import com.bm.project.payment.model.dto.PayValidationDto.PaymentItemDto;
import com.bm.project.payment.repository.CartRepository;
import com.bm.project.payment.repository.DeliveryRepository;
import com.bm.project.payment.repository.OrdersDetailRepository;
import com.bm.project.payment.repository.OrdersRepository;
import com.bm.project.payment.repository.PaymentRepository;
import com.bm.project.payment.repository.ProductRepository;
import com.bm.project.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
	
	private final OrdersRepository ordersRepo;
	private final MemberRepository memberRepo;
	private final ProductRepository productRepo;
	private final OrdersDetailRepository ordersDetailRepo;
	private final PaymentRepository paymentRepo;
	private final DeliveryRepository deliveryRepo;
	private final CartRepository cartRepo;
	
	private final Logger statLogger = LoggerFactory.getLogger("STAT_LOGGER");

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
		System.out.println("주문 번호 : " + orderNo);
		Orders orders = ordersRepo.findById(orderNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 주문건이 존재하지 않습니다."));
		
		orders.setPayStatus(reason); // "FAIL"
		ordersRepo.save(orders);
	}



	// 결제 사후 검증
	@Override
	@Transactional
	public void successPayment(PaySuccessDto successDto) {
		// 1. 결제 사후 검증 먼저
		Orders orders = ordersRepo.findById(successDto.getOrderNo())
				.orElseThrow(() -> new IllegalArgumentException("해당 주문건이 존재하지 않습니다."));
		
		int totalPrice = 0; // 총계산 다시 검증해서 넣기
		
		// 주문 상세 내역에서 하나씩 합산
		for(OrdersDetail detail : orders.getDetails()) {
			totalPrice += (detail.getItemPrice() * detail.getItemQuantity());
		}
		
		System.out.println("검증 금액 : " + totalPrice);
		System.out.println("결제 금액 : " + successDto.getPayPrice());
		
		// 금액 불일치 시 결제 취소
		if(totalPrice != successDto.getPayPrice()) {
			throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
		}
		
		// 검증에 성공했을 경우 DB에 각각 저장
		Payment payment = successDto.toPaymentEntity(orders);
		
		
		// 배송 정보가 들어온 경우에만 DB에 저장 (빈 값이 아닐 때)
		if(successDto.getDetailAddress() != null && !successDto.getDetailAddress().isEmpty()) {
			Delivery delivery = successDto.toDeliveryEntity(orders);
			deliveryRepo.save(delivery);
		} else {
			System.out.println("배송 정보 없음! 건너뜀");
		}
		
		orders.setPayStatus("PAID");
		orders.setPayment(paymentRepo.save(payment));
		
		Orders savedOrder = ordersRepo.save(orders); // 저장된 Orders
		
		Member member = savedOrder.getMember(); // 주문자
		
		for(OrdersDetail detail : savedOrder.getDetails()) {
			Product product = detail.getProduct();
			
			// 결제 로그 찍기
			sendStatisticsLog(member, product);
			
			try {
				cartRepo.deleteByMemberAndProduct(member, product);
			} catch (Exception e) {
				// 장바구니에 없는 상품일 수도 있으므로
				System.out.println("장바구니 삭제 중 예외 발생 (또는 해당 상품 없음): " + product.getProductNo());
			}
		}
	}



	// 구매하려는 상품의 상품명 / 가격 / 썸네일 가져오기
	@Override
	public List<PaymentItemDto> getPaymentItems(List<OrderItemDto> orderItemList) {
		// 세션에서 꺼낸 주문 목록(상품 번호, 수량)
		
		List<PaymentItemDto> paymentList = new ArrayList<>();
		
		for(PayValidationDto.OrderItemDto req : orderItemList) {
			Product product = productRepo.findById(req.getProductNo())
					.orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
			
			PaymentItemDto dto = PaymentItemDto.paymentItemToDto(product, req.getQuantity());
			
			// 리스트에 추가
			paymentList.add(dto);
		}
		return paymentList;
	}



	// 결제 조회
	@Override
	public Orders payComplete(String orderNo) {
		Orders orders = ordersRepo.findById(orderNo)
	            .orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다."));
		return orders;
	}
	
	// 연령대별 가장 많이 구매한 장르 로그찍기
	private void sendStatisticsLog(Member member, Product product) {
	    try {
	        // 1. 나이대 계산
	        String birth = member.getMemberBirth();
	        String ageGroupStr = "기타";

	        if (birth != null && birth.length() >= 4) {
	            int birthYear = Integer.parseInt(birth.substring(0, 4));
	            int currentYear = java.time.LocalDate.now().getYear();
	            int age = currentYear - birthYear + 1; // 한국 나이
	            int ageGroup = (age / 10) * 10; // 20, 30, 40... (연령대)
	            ageGroupStr = ageGroup + "대";
	        }

	        // 나중에 ELK에서 쉼표(,)로 쪼개기 쉽게
	        // 이건 통계 전용 로그
	        statLogger.info("[STAT_LOG],{},{}", 
	            ageGroupStr,
	            product.getCategory().getCategoryName()
	        );
	        
	        // 잘 찍히나 보기
	        log.info("통계 로그 전송 : {}대 / {}", ageGroupStr, product.getCategory().getCategoryName());
	        
	    } catch (Exception e) {
	        System.out.println("통계 로그 생성 중 오류: " + e.getMessage());
	    }
	}

}
