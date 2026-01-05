package com.bm.project.payment.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bm.project.dto.MemberDto;
import com.bm.project.dto.MemberDto.OrderMemberDto;
import com.bm.project.payment.entity.Orders;
import com.bm.project.payment.model.dto.PayValidationDto;
import com.bm.project.payment.model.dto.PayValidationDto.OrderItemDto;
import com.bm.project.payment.model.dto.PayValidationDto.PaymentItemDto;
import com.bm.project.payment.model.service.PaymentService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/order")
@Controller
public class OrderController {
	
	private final PaymentService paymentService;
	
	@Value("${portone.store-id}") 
    private String storeId;

    @Value("${portone.channel-key}")
    private String channelKey;
	
	// 결제창(배송 정보 입력창) 이동
	@GetMapping("/delivery")
	public String delivery(Model model, HttpSession session) {
		model.addAttribute("storeId", storeId);
		model.addAttribute("channelKey", channelKey);
		
//		System.out.println(session.getAttribute("orderMemberDto"));
//		System.out.println(session.getAttribute("orderItemList"));
		
		// 장바구니에서 비동기로 주문할 상품 및 회원 정보를 이곳으로 보내는데
		// 비동기로 페이지 이동을 하게 되면서 값을 유지할 방법이 세션 뿐이어서 세션에 담아서 이곳에 보내주고,
		// 바로 값을 변수에 저장하고 -> 세션 유지 되면 안 좋으니 세션에서 지워줌
		
		List<PayValidationDto.OrderItemDto> orderItemList = (List<OrderItemDto>) session.getAttribute("orderItemList");
		MemberDto.OrderMemberDto orderMemberDto = (OrderMemberDto) session.getAttribute("orderMemberDto");
		
		// 가져온 상품 번호와 수량으로 DB 조회 (이미지, 상품명, 가격, 주문 수량 등)
		List<PaymentItemDto> paymentList = paymentService.getPaymentItems(orderItemList);
		System.out.println("=== paymentList ===");
		System.out.println(paymentList);
		System.out.println("=== orderMemberDto ===");
		System.out.println(orderMemberDto);
		
		// 총 결제 금액
		int totalAmount = paymentList.stream().mapToInt(PaymentItemDto::getTotalPrice).sum();
		System.out.println(totalAmount);
		
		model.addAttribute("orderMember", orderMemberDto);
		model.addAttribute("paymentList", paymentList);
		model.addAttribute("totalAmount", totalAmount);
		
		return "cart_order/delivery";
	}
	
	
	// 결제 전 총금액 사전 검증
	@PostMapping("/validation")
	@ResponseBody
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
	@ResponseBody
	public ResponseEntity<String> failPayment(@RequestBody String failOrderNo) {
		
//		System.out.println("취소 또는 실패 주문번호 : " + failOrderNo);
		
		paymentService.failPayment(failOrderNo, "FAIL");
		return ResponseEntity.ok("결제가 취소 처리 되었습니다.");
	}
	
	// 결제 완료 후 사후 검증
	@PostMapping("/payment")
	@ResponseBody
	public ResponseEntity<String> successPayment(@RequestBody PayValidationDto.PaySuccessDto successDto, HttpSession session) {
		System.out.println("검증을 위한 DTO : " + successDto);
		paymentService.successPayment(successDto);
		
		// 세션에 등록했던 배송 정보 날리기
		session.removeAttribute("orderItemList");
		session.removeAttribute("orderMemberDto");
		
		return ResponseEntity.ok("결제가 성공적으로 처리 되었습니다!");
	}
	
	// 결제 완료창 이동
	@GetMapping("/complete/{orderNo}")
	public String payComplete(@PathVariable("orderNo") String orderNo, Model model) {
	    Orders order = paymentService.payComplete(orderNo);
	    System.out.println(order);
	    
	    model.addAttribute("order", order);
	    model.addAttribute("payment", order.getPayment());
	    model.addAttribute("delivery", order.getDelivery());
		return "cart_order/pay_completed";
	}
	
}
