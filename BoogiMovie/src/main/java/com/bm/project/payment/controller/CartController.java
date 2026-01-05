package com.bm.project.payment.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.bm.project.dto.MemberDto;
import com.bm.project.payment.model.dto.CartDto;
import com.bm.project.payment.model.dto.PayValidationDto;
import com.bm.project.payment.model.service.CartService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
	

    
    private final CartService service;
	
	// 장바구니 조회
	@GetMapping
	public String getCartList(@SessionAttribute("loginMember") MemberDto.LoginResult loginMember,
							  Model model) {
		
		// 로그인한 회원이 가지고 있는 장바구니 목록 조회
		List<CartDto.CartRespDto> cartList = service.findCartListByMemberNo(loginMember);
		System.out.println("=== 장바구니 목록 조회 ===\n" + cartList);
		
		model.addAttribute("cartList", cartList);
		
		return "cart_order/cart";
	}
	
	// 장바구니에서 아이템 삭제 (개별, 전체)
	@PostMapping("/delete")
	@ResponseBody
	public ResponseEntity<String> deleteCartItem(@RequestBody Map<String, List<Long>> params) {
//		System.out.println("params: " + params);
		
		List<Long> itemList = params.get("cartNoList");
		
//		System.out.println("itemList : " + itemList);
		
		service.deleteCartItem(itemList);
		return ResponseEntity.ok("장바구니에서 삭제 되었습니다.");
	}
	
	// 장바구니 아이템 수량 변경
	@PatchMapping("/setQuantity")
	@ResponseBody
	public ResponseEntity<String> updateQuantity(@RequestBody Map<String, Long> params) {
//		System.out.println(params);
		Long itemNo = params.get("itemNo");
		Long lQuantity = params.get("quantity");
		
		Integer quantity = lQuantity.intValue();
		
//		System.out.println("itemNo : " + itemNo);
//		System.out.println("quantity : " + quantity);
		
		service.updateQuantity(itemNo, quantity);
		
		return ResponseEntity.ok("수량 변경 성공");
	}
	
	// 장바구니에서 결제창으로 이동
	@PostMapping("/order")
	@ResponseBody
	public int cartToOrder(@RequestBody List<PayValidationDto.OrderItemDto> orderItemList,
							  @SessionAttribute("loginMember") MemberDto.LoginResult loginMember,
							  HttpSession session) {
		
//		System.out.println("======= 결제창 넘어오는 값 ======");
//		System.out.println(orderItemList);
		
		// 회원 정보(이름, 전화번호, 주소)
		MemberDto.OrderMemberDto orderMemberDto = service.getOrderMemberInfo(loginMember.getMemberNo());
		if(orderMemberDto != null) {
			session.setAttribute("orderMemberDto", orderMemberDto);
			session.setAttribute("orderItemList", orderItemList);
			return 1;
			
		} else {
			return 0;
		}
		
	}
	
	@PostMapping("/addCart")
	@ResponseBody
	public int addCart(
			@RequestBody Map<String, Object> paramMap,
		    @SessionAttribute(value = "loginMember", required = false) MemberDto.LoginResult loginMember
			) {
		
		if (loginMember == null) return -1;
		
		Long productNo = Long.valueOf(paramMap.get("productNo").toString());
		Integer quantity = Integer.valueOf(paramMap.get("quantity").toString());
		
		service.addCart(productNo, quantity, loginMember.getMemberNo());
		
		
		
		return 1;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
