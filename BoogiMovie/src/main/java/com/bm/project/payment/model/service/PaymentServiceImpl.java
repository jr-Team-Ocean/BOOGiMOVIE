package com.bm.project.payment.model.service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bm.project.entity.Member;
import com.bm.project.payment.entity.Orders;
import com.bm.project.payment.model.dto.PayValidationDto;
import com.bm.project.payment.repository.OrdersDetailRepository;
import com.bm.project.payment.repository.OrdersRepository;
import com.bm.project.payment.repository.PaymentRepository;
import com.bm.project.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	
	private final OrdersRepository ordersRepo;
	private final OrdersDetailRepository ordersDetailRepo;
	private final PaymentRepository paymentRepo;
	private final MemberRepository memberRepo;
	


	// 결제 전 총금액 사전 검증
	@Override
	@Transactional
	public PayValidationDto prePaymentValidation(PayValidationDto payValidation) {
		Member member = memberRepo.findById(payValidation.getMemberNo())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
		
		
		// 주문 번호 난수 (20251229-UUID 8자리)
		String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
							+ "-" + UUID.randomUUID().toString().substring(0, 8);
		
		Orders order = Orders.builder()
                .orderNo(orderNo)
                .member(member)
                .payStatus("READY") // 결제 대기
                .orderQuantity(0)
                .build();
		
		return null;
	}

}
