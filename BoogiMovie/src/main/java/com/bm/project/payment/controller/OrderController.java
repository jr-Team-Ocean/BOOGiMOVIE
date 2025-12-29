package com.bm.project.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bm.project.dto.MemberDto;
import com.bm.project.payment.model.dto.OrderRequestDto;
import com.bm.project.payment.model.dto.PayValidationDto;
import com.bm.project.payment.model.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {
	
	private final PaymentService paymentService;
	
	// 결제 전 총금액 사전 검증
	@PostMapping("/validation")
	public ResponseEntity<PayValidationDto.PayResponse> prePaymentValidation(@RequestBody PayValidationDto.PayRequest payValidation) {
		// js에서 보낸 회원 번호, 상품 리스트(각 상품의 상품 번호와 주문 수량 담겨서 옴)
		System.out.println("받은 데이터: " + payValidation); 
	    System.out.println("회원번호: " + payValidation.getMemberNo());
		PayValidationDto.PayResponse dto = paymentService.prePaymentValidation(payValidation);
		
		return ResponseEntity.ok(dto);
	}
	
	// 결제 진행 중 취소 또는 실패시
	@PostMapping("/fail")
	public ResponseEntity<String> failPayment(@RequestBody String orderNo) {
		paymentService.failPayment(orderNo, "FAIL");
		return ResponseEntity.ok("결제가 실패 처리 되었습니다.");
	}

}
