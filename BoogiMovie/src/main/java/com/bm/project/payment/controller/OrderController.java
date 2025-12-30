package com.bm.project.payment.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		
//		System.out.println("받은 데이터: " + payValidation); 
//	    System.out.println("회원번호: " + payValidation.getMemberNo());
		PayValidationDto.PayResponse dto = paymentService.prePaymentValidation(payValidation);
//		System.out.println(dto);
		

		return ResponseEntity.ok(dto);
	}
	
	// 결제 진행 중 취소 또는 실패시
	@PostMapping("/fail")
	public ResponseEntity<String> failPayment(@RequestBody String failOrderNo) {
		
//		System.out.println("취소 또는 실패 주문번호 : " + failOrderNo);
		
		paymentService.failPayment(failOrderNo, "FAIL");
		return ResponseEntity.ok("결제가 취소 처리 되었습니다.");
	}
	
	// 결제 완료 후 사후 검증
	@PostMapping("/payment")
	public ResponseEntity<String> successPayment(@RequestBody PayValidationDto.PaySuccessDto successDto) {
		System.out.println("검증을 위한 DTO : " + successDto);
		paymentService.successPayment(successDto);
		return ResponseEntity.ok("결제가 성공적으로 처리 되었습니다!");
	}
	
}
