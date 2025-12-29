package com.bm.project.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bm.project.dto.MemberDto;
import com.bm.project.payment.model.dto.OrderRequestDto;
import com.bm.project.payment.model.dto.PayValidationDto;
import com.bm.project.payment.model.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
	
	private final PaymentService paymentService;
	
	// 결제 전 총금액 사전 검증
	@PostMapping("/validation")
	public ResponseEntity<PayValidationDto> prePaymentValidation(@RequestBody PayValidationDto payValidation) {
		PayValidationDto dto = paymentService.prePaymentValidation(payValidation);
		
		return ResponseEntity.ok(dto);
	}
	
	
	// 결제 요청
//	@PostMapping("/payment")
//	public ResponseEntity<String> productOrder(@RequestBody OrderRequestDto orderDto,
//											   @SessionAttribute("loginMember") MemberDto.LoginResult loginMember,
//											   /* 주문자 정보 */
//											   RedirectAttributes ra) {
//		
//		try {
//			paymentService.processOrder(orderDto, loginMember);
//			// 비동기 요청 받고 ok 반환
//			return ResponseEntity.ok("주문이 성공적으로 완료 되었습니다!");
//			
//		} catch (IllegalArgumentException e) {
//			// 결제 요청 실패 또는 회원 정보를 찾을 수 없는 경우
//			return ResponseEntity.badRequest().body(e.getMessage());
//		}
//		
//	}

}
